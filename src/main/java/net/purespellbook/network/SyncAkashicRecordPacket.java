package net.purespellbook.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.purespellbook.akashic.AkashicRecordSavedData;
import net.purespellbook.akashic.ClientAkashicRecordData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Syncs a player's 72 Akashic Record slots from server to client.
 */
public class SyncAkashicRecordPacket {
    private final List<ItemStack> stacks;

    public SyncAkashicRecordPacket(List<ItemStack> stacks) {
        this.stacks = new ArrayList<>(AkashicRecordSavedData.TOTAL_SLOTS);
        for (int i = 0; i < AkashicRecordSavedData.TOTAL_SLOTS; i++) {
            this.stacks.add(i < stacks.size() ? stacks.get(i).copy() : ItemStack.EMPTY);
        }
    }

    public SyncAkashicRecordPacket(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        this.stacks = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.stacks.add(buf.readItem());
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeVarInt(stacks.size());
        for (ItemStack stack : stacks) {
            buf.writeItem(stack);
        }
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                ClientAkashicRecordData.setAll(this.stacks)));
        ctx.setPacketHandled(true);
        return true;
    }
}
