package net.purespellbook.akashic;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.api.spells.SpellData;
import io.redspace.ironsspellbooks.capabilities.magic.MagicManager;
import io.redspace.ironsspellbooks.item.Scroll;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.purespellbook.spell.AkashicRecordAccessSpell;
import net.purespellbook.spell.AkashicRecordExecSpell;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Shared helper for reading stored scrolls and computing combined mana, cast time,
 * and cooldown for Akashic Record Execution spells.
 */
public final class AkashicRecordHelper {
    private AkashicRecordHelper() {}

    public static boolean isValidScroll(ItemStack stack) {
        return isValidScroll(stack, null);
    }

    public static boolean isValidScroll(ItemStack stack, @Nullable LivingEntity entity) {
        if (stack.isEmpty() || !(stack.getItem() instanceof Scroll)) {
            return false;
        }
        if (ISpellContainer.isSpellContainer(stack)) {
            SpellData data = ISpellContainer.get(stack).getSpellAtIndex(0);
            if (data != null && data.getSpell() != null) {
                AbstractSpell spell = data.getSpell();
                if (spell instanceof AkashicRecordAccessSpell || spell instanceof AkashicRecordExecSpell) {
                    return false;
                }
                if (spell.getCastType() == CastType.CONTINUOUS || isRecastSpell(spell, data.getLevel(), entity)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isRecastSpell(AbstractSpell spell, int spellLevel, @Nullable LivingEntity entity) {
        try {
            return spell.getRecastCount(spellLevel, entity) > 0;
        } catch (Exception ignored) {
            return false;
        }
    }

    @Nullable
    public static NonNullList<ItemStack> getAllSlots(@Nullable LivingEntity entity) {
        if (entity instanceof ServerPlayer serverPlayer && serverPlayer.getServer() != null) {
            return AkashicRecordSavedData.get(serverPlayer.getServer()).getRecord(serverPlayer.getUUID());
        }
        if (entity != null && entity.level().isClientSide) {
            return ClientAkashicRecordData.getAll();
        }
        return null;
    }

    @Nullable
    public static Player resolveClientPlayerFallback() {
        final Player[] holder = new Player[1];
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            holder[0] = net.minecraft.client.Minecraft.getInstance().player;
        });
        return holder[0];
    }

    public static List<SpellData> getPageSpells(@Nullable LivingEntity entity, int pageIndex) {
        List<SpellData> result = new ArrayList<>();
        if (pageIndex < 0 || pageIndex >= AkashicRecordSavedData.PAGE_COUNT) {
            return result;
        }
        NonNullList<ItemStack> slots = getAllSlots(entity);
        if (slots == null) {
            return result;
        }
        int start = pageIndex * AkashicRecordSavedData.SLOTS_PER_PAGE;
        int end = start + AkashicRecordSavedData.SLOTS_PER_PAGE;
        for (int i = start; i < end && i < slots.size(); i++) {
            ItemStack stack = slots.get(i);
            if (isValidScroll(stack) && ISpellContainer.isSpellContainer(stack)) {
                ISpellContainer container = ISpellContainer.get(stack);
                SpellData data = container.getSpellAtIndex(0);
                if (data != null && !data.equals(SpellData.EMPTY) && data.getSpell() != null) {
                    result.add(data);
                }
            }
        }
        return result;
    }

    public static int getCombinedManaCost(@Nullable LivingEntity entity, int pageIndex, int baseMana) {
        int total = baseMana;
        for (SpellData data : getPageSpells(entity, pageIndex)) {
            total += Math.max(0, data.getSpell().getManaCost(data.getLevel()));
        }
        return total;
    }

    public static int getCombinedCastTime(@Nullable LivingEntity entity, int pageIndex) {
        int total = 0;
        for (SpellData data : getPageSpells(entity, pageIndex)) {
            AbstractSpell spell = data.getSpell();
            total += Math.max(0, spell.getEffectiveCastTime(data.getLevel(), entity));
        }
        return total;
    }

    public static int getCombinedEffectiveCooldown(Player player, int pageIndex,
                                                   io.redspace.ironsspellbooks.api.spells.CastSource castSource) {
        int total = 0;
        for (SpellData data : getPageSpells(player, pageIndex)) {
            total += Math.max(0, MagicManager.getEffectiveSpellCooldown(data.getSpell(), player, castSource));
        }
        return total;
    }
}
