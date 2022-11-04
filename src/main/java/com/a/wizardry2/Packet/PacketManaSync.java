package com.a.wizardry2.Packet;

import com.a.wizardry2.WizardryROTN;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import com.a.wizardry2.Packet.PacketManaSync.Message;

public class PacketManaSync implements IMessageHandler<Message, IMessage> {


    @Override
    public IMessage onMessage(Message message, MessageContext ctx) {
        // Just to make sure that the side is correct
        // Just to make sure that the side is correct
        if(ctx.side.isClient()){
            // Using a fully qualified name is a good course of action here; we don't really want to clutter the proxy
            // methods any more than necessary.
            net.minecraft.client.Minecraft.getMinecraft().addScheduledTask(() -> WizardryROTN.proxy.handlePacketManaSync(message));
        }

        return null;
    }

    public static class Message implements IMessage {

        public int entityID;
        public float mana;
        public float manaMax;



        // This constructor is required otherwise you'll get errors (used somewhere in fml through reflection)
        public Message(){
        }

        public Message(EntityPlayer player, float mana, float manaMax){
            this.entityID = player.getEntityId();
            this.mana = mana;
            this.manaMax = manaMax;
        }


        @Override
        public void fromBytes(ByteBuf buf) {
            this.entityID = buf.readInt();
            this.mana = buf.readFloat();
            this.manaMax = buf.readFloat();
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(entityID);
            buf.writeFloat(mana);
            buf.writeFloat(manaMax);

        }
    }
}
