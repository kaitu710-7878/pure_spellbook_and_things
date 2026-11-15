package net.purespellbook;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.purespellbook.block.PureScrollForgeRenderer;
import net.purespellbook.gui.PureScrollForgeScreen;
import net.purespellbook.network.ModNetwork;
import net.purespellbook.registry.*;

@Mod(PureSpellbookMod.MOD_ID)
public class PureSpellbookMod {
    public static final String MOD_ID = "pure_spellbook_and_things";

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final RegistryObject<CreativeModeTab> PURE_TAB = CREATIVE_TABS.register("pure_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + MOD_ID))
                    .icon(() -> new ItemStack(ModItems.PURE_SPELLBOOK.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.PURE_SCROLL_FORGE.get());
                        output.accept(ModItems.PURE_SPELLBOOK.get());
                        for (RegistryObject<AbstractSpell> spellObj : ModSpells.allPureSpells()) {
                            ItemStack scroll = new ItemStack(ItemRegistry.SCROLL.get());
                            ISpellContainer.createScrollContainer(spellObj.get(), 1, scroll);
                            output.accept(scroll);
                        }
                    })
                    .build());

    public PureSpellbookMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModAttributes.register(modEventBus);
        ModMobEffects.register(modEventBus);
        ModSchools.register(modEventBus);
        ModSpells.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModLootModifiers.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::onAttributeModification);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModNetwork::register);
    }

    private void onAttributeModification(final EntityAttributeModificationEvent event) {
        event.getTypes().forEach(entityType -> {
            event.add(entityType, ModAttributes.OVER_HP.get());
            event.add(entityType, ModAttributes.PURE_SPELL_POWER.get());
            event.add(entityType, ModAttributes.PURE_MAGIC_RESIST.get());
        });
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() ->
                    MenuScreens.register(ModMenuTypes.PURE_SCROLL_FORGE_MENU.get(), PureScrollForgeScreen::new));
        }

        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(ModBlockEntities.PURE_SCROLL_FORGE.get(), PureScrollForgeRenderer::new);
        }

        @SubscribeEvent
        public static void onRegisterAdditionalModels(ModelEvent.RegisterAdditional event) {
            event.register(new ResourceLocation(MOD_ID, "item/scroll_pure"));
        }
    }
}
