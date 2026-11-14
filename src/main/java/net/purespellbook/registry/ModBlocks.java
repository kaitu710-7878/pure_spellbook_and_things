package net.purespellbook.registry;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.purespellbook.PureSpellbookMod;
import net.purespellbook.block.PureScrollForgeBlock;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, PureSpellbookMod.MOD_ID);

    public static final RegistryObject<Block> PURE_SCROLL_FORGE =
            BLOCKS.register("pure_scroll_forge", PureScrollForgeBlock::new);

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
