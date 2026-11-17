package net.purespellbook.registry;

import io.redspace.ironsspellbooks.item.UpgradeOrbItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.purespellbook.PureSpellbookMod;
import net.purespellbook.item.PureRuneItem;
import net.purespellbook.item.PureScrollForgeBlockItem;
import net.purespellbook.item.PureSpellbookItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, PureSpellbookMod.MOD_ID);

    public static final RegistryObject<Item> PURE_SCROLL_FORGE = ITEMS.register("pure_scroll_forge",
            () -> new PureScrollForgeBlockItem(ModBlocks.PURE_SCROLL_FORGE.get(), new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> PURE_SPELLBOOK = ITEMS.register("pure_spellbook",
            PureSpellbookItem::new);

    public static final RegistryObject<Item> PURE_RUNE = ITEMS.register("pure_rune",
            PureRuneItem::new);

    public static final RegistryObject<Item> PURE_UPGRADE_ORB = ITEMS.register("pure_upgrade_orb",
            () -> new UpgradeOrbItem(new Item.Properties().rarity(Rarity.UNCOMMON), ModUpgradeOrbTypes.PURE_SPELL_POWER));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
