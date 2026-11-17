package net.purespellbook.registry;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.purespellbook.PureSpellbookMod;
import net.purespellbook.gui.AkashicRecordMenu;
import net.purespellbook.gui.PureScrollForgeMenu;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, PureSpellbookMod.MOD_ID);

    public static final RegistryObject<MenuType<PureScrollForgeMenu>> PURE_SCROLL_FORGE_MENU =
            MENUS.register("pure_scroll_forge", () -> IForgeMenuType.create(PureScrollForgeMenu::new));

    public static final RegistryObject<MenuType<AkashicRecordMenu>> AKASHIC_RECORD_MENU =
            MENUS.register("akashic_record", () -> IForgeMenuType.create(AkashicRecordMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
