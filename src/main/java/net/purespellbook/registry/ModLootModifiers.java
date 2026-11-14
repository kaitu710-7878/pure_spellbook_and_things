package net.purespellbook.registry;

import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.purespellbook.PureSpellbookMod;
import net.purespellbook.loot.PureScrollLootModifier;

public class ModLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, PureSpellbookMod.MOD_ID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> PURE_SCROLL_LOOT =
            LOOT_MODIFIER_SERIALIZERS.register("pure_scroll_loot", () -> PureScrollLootModifier.CODEC);

    public static void register(IEventBus eventBus) {
        LOOT_MODIFIER_SERIALIZERS.register(eventBus);
    }
}
