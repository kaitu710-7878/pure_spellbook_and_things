package net.purespellbook.registry;

import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.SchoolType;
import io.redspace.ironsspellbooks.damage.ISSDamageTypes;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.purespellbook.PureSpellbookMod;

public class ModSchools {
    public static final DeferredRegister<SchoolType> SCHOOLS =
            DeferredRegister.create(SchoolRegistry.SCHOOL_REGISTRY_KEY, PureSpellbookMod.MOD_ID);

    public static final ResourceLocation PURE_RESOURCE =
            new ResourceLocation(PureSpellbookMod.MOD_ID, "pure");

    public static final TagKey<Item> PURE_FOCUS =
            ItemTags.create(new ResourceLocation(PureSpellbookMod.MOD_ID, "pure_focus"));

    public static final RegistryObject<SchoolType> PURE = SCHOOLS.register("pure", () -> new SchoolType(
            PURE_RESOURCE,
            PURE_FOCUS,
            Component.translatable("school." + PureSpellbookMod.MOD_ID + ".pure")
                    .withStyle(Style.EMPTY.withColor(0xE0F7FF)),
            ModAttributes.PURE_SPELL_POWER,
            ModAttributes.PURE_MAGIC_RESIST,
            SoundRegistry.HOLY_CAST,
            ISSDamageTypes.ELDRITCH_MAGIC,
            false,
            false
    ));

    public static void register(IEventBus eventBus) {
        SCHOOLS.register(eventBus);
    }
}
