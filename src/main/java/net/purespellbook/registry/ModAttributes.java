package net.purespellbook.registry;

import io.redspace.ironsspellbooks.api.attribute.MagicPercentAttribute;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.purespellbook.PureSpellbookMod;

public class ModAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(ForgeRegistries.ATTRIBUTES, PureSpellbookMod.MOD_ID);

    public static final RegistryObject<Attribute> OVER_HP = ATTRIBUTES.register("over_hp",
            () -> new RangedAttribute("attribute.name." + PureSpellbookMod.MOD_ID + ".over_hp", 0.0D, 0.0D, 1000000.0D)
                    .setSyncable(true));

    public static final RegistryObject<Attribute> PURE_SPELL_POWER = ATTRIBUTES.register("pure_spell_power",
            () -> new MagicPercentAttribute("attribute." + PureSpellbookMod.MOD_ID + ".pure_spell_power", 1.0D, -100.0D, 100.0D)
                    .setSyncable(true));

    public static final RegistryObject<Attribute> PURE_MAGIC_RESIST = ATTRIBUTES.register("pure_magic_resist",
            () -> new MagicPercentAttribute("attribute." + PureSpellbookMod.MOD_ID + ".pure_magic_resist", 1.0D, -100.0D, 100.0D)
                    .setSyncable(true));

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }
}
