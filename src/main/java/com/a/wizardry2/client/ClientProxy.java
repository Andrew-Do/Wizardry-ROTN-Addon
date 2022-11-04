package com.a.wizardry2.client;

import com.a.wizardry2.capability.ManaProvider;
import com.a.wizardry2.Packet.PacketManaSync;
import com.a.wizardry2.main.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {

    @Override
    public void preinit() {
        ClientRegistry.registerKeyBinding(ClientEventHandler.KEY_OPEN);
    }

    public void handlePacketManaSync(PacketManaSync.Message message)
    {
        World world = Minecraft.getMinecraft().world;
        Entity player = world.getEntityByID(message.entityID);

        if(player instanceof EntityPlayer){
            player.getCapability(ManaProvider.MANA_CAPABILITY, null).sync(message.mana, message.manaMax);
        }
    }

}
