package com.a.wizardry2.client;

import com.a.wizardry2.Packet.PacketHandler;
import com.a.wizardry2.Packet.PacketManaSync;
import com.a.wizardry2.Packet.PacketOpenGui;
import com.a.wizardry2.Packet.RequestAction;
import com.a.wizardry2.WizardryROTN;
import electroblob.wizardry.Wizardry;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientEventHandler {
    public static final KeyBinding KEY_OPEN = new KeyBinding("key." + WizardryROTN.MODID + ".open", KeyConflictContext.IN_GAME, KeyModifier.NONE, Keyboard.KEY_O, "Wizardry");

    @SubscribeEvent
    public static void onKey(InputEvent.KeyInputEvent event) {
        if (KEY_OPEN.isPressed())
            PacketHandler.net.sendToServer(new PacketOpenGui.Message(RequestAction.OPEN_BACKPACK));

    }


}

