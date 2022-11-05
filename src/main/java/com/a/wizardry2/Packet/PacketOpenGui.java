package com.a.wizardry2.Packet;

import com.a.wizardry2.WizardryROTN;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOpenGui  implements IMessageHandler<PacketOpenGui.Message, IMessage> {
    @Override
    public IMessage onMessage(PacketOpenGui.Message message, MessageContext ctx) {
        if (message.action == null)
            return null;

        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> message.action.handle(ctx.getServerHandler()));
        return null;
    }

    public static class Message implements IMessage {

        private RequestAction action;

        public Message(){}

        // This constructor is required otherwise you'll get errors (used somewhere in fml through reflection)
        public Message(RequestAction action){
            this.action = action;
        }


        @Override public void fromBytes(ByteBuf buf) {
            this.action = RequestAction.getAction(buf.readInt());
        }
        @Override public void toBytes(ByteBuf buf) {
            buf.writeInt(action.ordinal());
        }
    }
}
