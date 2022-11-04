package com.a.wizardry2.main;

import com.a.wizardry2.Packet.PacketManaSync;
import com.a.wizardry2.WizardryROTN;
import com.a.wizardry2.capability.*;
import com.a.wizardry2.container.GuiHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.network.NetworkRegistry;

/**
 * Common proxy class
 */
public class CommonProxy
{
    public void preinit() {}
    public void init()
    {
        registerCapabilities();
        MinecraftForge.EVENT_BUS.register(new CapabilityHandler());

        NetworkRegistry.INSTANCE.registerGuiHandler(WizardryROTN.instance, new GuiHandler());
    }

    public void handlePacketManaSync(PacketManaSync.Message message){}

    private void registerCapabilities()
    {
        CapabilityManager.INSTANCE.register(IMana.class, new ManaStorage(), Mana.class);

    }
}
