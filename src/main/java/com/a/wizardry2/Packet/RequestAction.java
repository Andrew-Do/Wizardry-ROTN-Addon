package com.a.wizardry2.Packet;

import com.a.wizardry2.WizardryROTN;
import com.a.wizardry2.container.GuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.EnumHand;

import javax.annotation.Nullable;

@SuppressWarnings("ConstantConditions")
public enum RequestAction {

    OPEN_BACKPACK {
        @Override
        public void handle(NetHandlerPlayServer serverContext) {
            EntityPlayer player = serverContext.player;

            //no backpack equipped, open any pack in the inventory
            player.openGui(WizardryROTN.instance, GuiHandler.OPEN_GUI_WAND_ID, player.getEntityWorld(), 0, 0, 0);
        }
    };

    private static final RequestAction[] VALUES = values();

    public abstract void handle(NetHandlerPlayServer serverContext);

    @Nullable public static RequestAction getAction(int index) {
        if (index < 0 || index >= VALUES.length)
            return null;

        return VALUES[index];
    }
}
