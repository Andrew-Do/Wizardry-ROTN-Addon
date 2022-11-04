package com.a.wizardry2.capability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public interface IWand {
    boolean onSlotChanged(int slotNumber, ItemStack wand, ItemStack item, EntityPlayer player);
}
