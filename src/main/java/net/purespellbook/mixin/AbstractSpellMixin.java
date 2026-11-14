package net.purespellbook.mixin;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraft.world.entity.player.Player;
import net.purespellbook.registry.ModMobEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractSpell.class, remap = false)
public class AbstractSpellMixin {
    @Inject(method = "canBeInterrupted", at = @At("HEAD"), cancellable = true)
    private void pureSpellbook$preventInterruptionWhenEmpowered(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (player != null && player.hasEffect(ModMobEffects.EMPOWERED.get())) {
            cir.setReturnValue(false);
        }
    }
}
