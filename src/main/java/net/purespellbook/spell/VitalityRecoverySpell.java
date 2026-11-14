package net.purespellbook.spell;

import dev.xkmc.l2library.base.effects.api.InherentEffect;
import io.redspace.ironsspellbooks.api.events.SpellHealEvent;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellAnimations;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.api.util.Utils;
import io.redspace.ironsspellbooks.util.ModTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;

public class VitalityRecoverySpell extends AbstractPureSpell {
    public VitalityRecoverySpell() {
        super("vitality_recovery", 45.0D, 370, 60);
    }

    @Override
    public CastType getCastType() {
        return CastType.LONG;
    }

    public float getHealAmount(LivingEntity caster) {
        return 10.0F * getEntityPowerMultiplier(caster);
    }

    public int getFoodAmount(LivingEntity caster) {
        return Math.max(1, Math.round(5.0F * getEntityPowerMultiplier(caster)));
    }

    public float getSaturationAmount(LivingEntity caster) {
        return 5.0F * getEntityPowerMultiplier(caster);
    }

    @Override
    public List<MutableComponent> getUniqueInfo(int spellLevel, LivingEntity caster) {
        return List.of(
                Component.translatable("ui.irons_spellbooks.healing", Utils.stringTruncation(getHealAmount(caster), 1)),
                Component.translatable("ui.pure_spellbook_and_things.food_recovery", getFoodAmount(caster),
                        Utils.stringTruncation(getSaturationAmount(caster), 1)),
                Component.translatable("ui.pure_spellbook_and_things.cleanse_debuffs")
        );
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity, CastSource castSource, MagicData playerMagicData) {
        float heal = getHealAmount(entity);
        MinecraftForge.EVENT_BUS.post(new SpellHealEvent(entity, entity, heal, getSchoolType()));
        entity.heal(heal);

        if (entity instanceof Player player) {
            FoodData foodData = player.getFoodData();
            int newFood = Math.min(20, foodData.getFoodLevel() + getFoodAmount(entity));
            foodData.setFoodLevel(newFood);
            float newSat = Math.min((float) newFood, foodData.getSaturationLevel() + getSaturationAmount(entity));
            foodData.setSaturation(newSat);
        }

        // Cleanse harmful debuffs (respecting CLEANSE_IMMUNE and InherentEffect)
        List<MobEffect> toRemove = entity.getActiveEffects().stream()
                .map(inst -> inst.getEffect())
                .filter(effect -> effect.getCategory() == MobEffectCategory.HARMFUL
                        && !(effect instanceof InherentEffect)
                        && !BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect).is(ModTags.CLEANSE_IMMUNE))
                .toList();
        toRemove.forEach(entity::removeEffect);

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }

    @Override
    public AnimationHolder getCastStartAnimation() {
        return SpellAnimations.SELF_CAST_ANIMATION;
    }
}
