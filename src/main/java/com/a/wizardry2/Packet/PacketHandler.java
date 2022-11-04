package com.a.wizardry2.Packet;

import com.a.wizardry2.WizardryROTN;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.packet.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
    public static SimpleNetworkWrapper net;

    public static void initPackets(){
        net = NetworkRegistry.INSTANCE.newSimpleChannel(WizardryROTN.MODID.toUpperCase());
        registerMessage(PacketManaSync.class, PacketManaSync.Message.class);
        registerMessage(PacketOpenGui.class, PacketOpenGui.Message.class);
    }

    private static int nextPacketId = 0;

    private static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(
            Class<? extends IMessageHandler<REQ, REPLY>> packet, Class<REQ> message){
        net.registerMessage(packet, message, nextPacketId, Side.CLIENT);
        net.registerMessage(packet, message, nextPacketId, Side.SERVER);
        nextPacketId++;
    }
}
