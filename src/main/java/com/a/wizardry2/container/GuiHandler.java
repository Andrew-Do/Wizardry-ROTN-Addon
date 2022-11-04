package com.a.wizardry2.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

    public static final int OPEN_GUI_WAND_ID = 0;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int handId, int unused1, int unused2) {
        EnumHand hand = handId == 1 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;

        switch (ID) {
            case OPEN_GUI_WAND_ID: {
                //get which backpack to open (details in method called)
                return new ContainerWand(player.inventory, player.getHeldItem(hand));
            }

        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int handId, int unused1, int unused2) {
        EnumHand hand = handId == 1 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;

        switch (ID) {
            case OPEN_GUI_WAND_ID: {
                ContainerWand container = new ContainerWand(player.inventory, player.getHeldItem(hand));
                GuiWand instance = new GuiWand(container);
                container.setClientGui(instance);
                return instance;
            }
        }
        return null;
    }
}
