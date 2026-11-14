package net.purespellbook.network;

import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.purespellbook.gui.PureScrollForgeMenu;

import java.util.function.Supplier;

public class SelectPureSpellPacket {
    private final BlockPos pos;
    private final String spellId;

    public SelectPureSpellPacket(BlockPos pos, String spellId) {
        this.pos = pos;
        this.spellId = spellId;
    }

    public SelectPureSpellPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.spellId = buf.readUtf();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeUtf(this.spellId);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.containerMenu instanceof PureScrollForgeMenu menu
                    && menu.blockEntity.getBlockPos().equals(pos)) {
                AbstractSpell spell = SpellRegistry.getSpell(spellId);
                menu.setRecipeSpell(spell);
            }
        });
        return true;
    }
}
