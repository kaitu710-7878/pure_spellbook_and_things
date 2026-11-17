package net.purespellbook.akashic;

import net.minecraft.world.entity.player.Player;

/**
 * ThreadLocal player context used when AbstractSpell.getManaCost(int) is invoked
 * during cast initiation or client UI rendering for Akashic Record Execution spells.
 */
public final class AkashicCastContext {
    private static final ThreadLocal<Player> CURRENT_PLAYER = new ThreadLocal<>();

    private AkashicCastContext() {}

    public static void setPlayer(Player player) {
        CURRENT_PLAYER.set(player);
    }

    public static Player getPlayer() {
        return CURRENT_PLAYER.get();
    }

    public static void clear() {
        CURRENT_PLAYER.remove();
    }
}
