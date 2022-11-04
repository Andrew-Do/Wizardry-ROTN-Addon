package com.a.wizardry2.container;

import electroblob.wizardry.inventory.ContainerArcaneWorkbench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;

public class SpellBindEvent extends PlayerContainerEvent {

    public SpellBindEvent(EntityPlayer player, ContainerWand container){
        super(player, container);
    }

}