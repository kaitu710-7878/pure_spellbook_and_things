package net.purespellbook.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.purespellbook.gui.AkashicRecordMenu;

import java.util.function.Supplier;

/**
 * Switches the active page (0..3) in an open AkashicRecordMenu.
 */
public class SwitchAkashicPagePacket {
    private final int page;

    public SwitchAkashicPagePacket(int page) {
        this.page = page;
    }

    public SwitchAkashicPagePacket(FriendlyByteBuf buf) {
        this.page = buf.readVarInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeVarInt(this.page);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null && player.containerMenu instanceof AkashicRecordMenu menu) {
                menu.setPage(this.page);
            }
        });
        ctx.setPacketHandled(true);
        return true;
    }
}
