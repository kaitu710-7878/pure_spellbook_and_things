package net.purespellbook.registry;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.purespellbook.PureSpellbookMod;
import net.purespellbook.block.PureScrollForgeBlockEntity;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, PureSpellbookMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<PureScrollForgeBlockEntity>> PURE_SCROLL_FORGE =
            BLOCK_ENTITIES.register("pure_scroll_forge",
                    () -> BlockEntityType.Builder.of(PureScrollForgeBlockEntity::new, ModBlocks.PURE_SCROLL_FORGE.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
