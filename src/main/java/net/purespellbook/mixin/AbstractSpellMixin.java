package net.purespellbook.mixin;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastResult;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.purespellbook.akashic.AkashicCastContext;
import net.purespellbook.registry.ModMobEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractSpell.class, remap = false)
public class AbstractSpellMixin {
    @Inject(method = "canBeInterrupted", at = @At("HEAD"), cancellable = true)
    private void pureSpellbook$preventInterruptionWhenEmpowered(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (player != null && player.hasEffect(ModMobEffects.EMPOWERED.get())) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "attemptInitiateCast", at = @At("HEAD"))
    private void pureSpellbook$beforeAttemptInitiateCast(ItemStack stack, int spellLevel, Level level, Player player,
                                                         CastSource castSource, boolean triggerCooldown, String castingEquipmentSlot,
                                                         CallbackInfoReturnable<Boolean> cir) {
        AkashicCastContext.setPlayer(player);
    }

    @Inject(method = "attemptInitiateCast", at = @At("RETURN"))
    private void pureSpellbook$afterAttemptInitiateCast(ItemStack stack, int spellLevel, Level level, Player player,
                                                        CastSource castSource, boolean triggerCooldown, String castingEquipmentSlot,
                                                        CallbackInfoReturnable<Boolean> cir) {
        AkashicCastContext.clear();
    }

    @Inject(method = "castSpell", at = @At("HEAD"))
    private void pureSpellbook$beforeCastSpell(Level world, int spellLevel, ServerPlayer serverPlayer,
                                               CastSource castSource, boolean triggerCooldown, CallbackInfo ci) {
        AkashicCastContext.setPlayer(serverPlayer);
    }

    @Inject(method = "castSpell", at = @At("RETURN"))
    private void pureSpellbook$afterCastSpell(Level world, int spellLevel, ServerPlayer serverPlayer,
                                              CastSource castSource, boolean triggerCooldown, CallbackInfo ci) {
        AkashicCastContext.clear();
    }

    @Inject(method = "canBeCastedBy", at = @At("HEAD"))
    private void pureSpellbook$beforeCanBeCastedBy(int spellLevel, CastSource castSource, MagicData playerMagicData,
                                                   Player player, CallbackInfoReturnable<CastResult> cir) {
        AkashicCastContext.setPlayer(player);
    }

    @Inject(method = "canBeCastedBy", at = @At("RETURN"))
    private void pureSpellbook$afterCanBeCastedBy(int spellLevel, CastSource castSource, MagicData playerMagicData,
                                                  Player player, CallbackInfoReturnable<CastResult> cir) {
        AkashicCastContext.clear();
    }
}
