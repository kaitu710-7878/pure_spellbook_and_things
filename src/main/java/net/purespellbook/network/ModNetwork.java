package net.purespellbook.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.purespellbook.PureSpellbookMod;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(PureSpellbookMod.MOD_ID, "messages"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        CHANNEL.messageBuilder(SelectPureSpellPacket.class, packetId++, NetworkDirection.PLAY_TO_SERVER)
                .decoder(SelectPureSpellPacket::new)
                .encoder(SelectPureSpellPacket::toBytes)
                .consumerMainThread(SelectPureSpellPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        CHANNEL.send(PacketDistributor.SERVER.noArg(), message);
    }
}
