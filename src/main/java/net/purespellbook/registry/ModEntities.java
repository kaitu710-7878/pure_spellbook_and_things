package net.purespellbook.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.purespellbook.PureSpellbookMod;
import net.purespellbook.entity.LegacyEchoStrikeEntity;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, PureSpellbookMod.MOD_ID);

    public static final RegistryObject<EntityType<LegacyEchoStrikeEntity>> LEGACY_ECHO_STRIKE =
            ENTITIES.register("legacy_echo_strike", () -> EntityType.Builder.<LegacyEchoStrikeEntity>of(
                            LegacyEchoStrikeEntity::new, MobCategory.MISC)
                    .sized(1.0F, 1.0F)
                    .clientTrackingRange(64)
                    .build(new ResourceLocation(PureSpellbookMod.MOD_ID, "legacy_echo_strike").toString()));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}
