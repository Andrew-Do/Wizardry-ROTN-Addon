package com.a.wizardry2.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class WandProvider implements ICapabilitySerializable<NBTTagCompound> {

    public WandProvider(ItemStack stack)
    {
        this.stack = stack;
        this.handler = new ItemStackHandler(MAX_SIZE);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return this.hasCapability(capability, facing) ? (T) this.handler : null;
    }

    public NBTTagCompound serializeNBT() {
        if (!this.stack.hasTagCompound()) this.stack.setTagCompound(new NBTTagCompound());
        return this.handler.serializeNBT();
    }

    public void deserializeNBT(NBTTagCompound nbt) {
        this.handler.deserializeNBT(nbt);
    }

    //fields
    protected ItemStack stack;
    protected final ItemStackHandler handler;
    final int MAX_SIZE = 9; //wand 0-7 + upgrade slot 8, 0-8 is 9 slots

}
