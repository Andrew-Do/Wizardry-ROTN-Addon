package com.a.wizardry2.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class SlotWandItem extends SlotItemHandler {
    public SlotWandItem(ContainerWand container, IItemHandler itemHandler, int index, int x, int y, int stackLimit, Item... allowedItems) {
        super(itemHandler, index, x, y);
        this.container = container;
        this.stackLimit = stackLimit;
        this.type = Type.ITEMS;
        this.items = allowedItems;
    }

    public SlotWandItem(ContainerWand container, IItemHandler itemHandler, int index, int x, int y, int stackLimit, Class<? extends Item>... allowedItemClasses) {
        super(itemHandler, index, x, y);
        this.container = container;
        this.stackLimit = stackLimit;
        this.type = Type.CLASSES;
        this.itemClasses = allowedItemClasses;
    }

    @Override
    public void putStack(ItemStack stack){
        super.putStack(stack);
        this.container.onSlotChanged(slotNumber, stack, true);
    }

    @Override
    public ItemStack onTake(EntityPlayer player, ItemStack stack){
        ItemStack result = super.onTake(player, stack);
        this.container.onSlotChanged(slotNumber, ItemStack.EMPTY, false);
        return result;
    }

    public int getSlotStackLimit(){
        return stackLimit;
    }

    public boolean isItemValid(ItemStack stack){

        if (type == Type.ITEMS)
        {
            for(Item item : items){
                if(stack.getItem() == item){
                    return true;
                }
            }

        }

        if (type == Type.CLASSES)
        {
            for(Class<? extends Item> itemClass : itemClasses){
                if(itemClass.isAssignableFrom(stack.getItem().getClass())){
                    return true;
                }
            }
        }

        return false;
    }

    public enum Type
    {
        ITEMS,
        CLASSES
    }

    private Item[] items = null;
    private Class<? extends Item>[] itemClasses = null;

    private Type type;

    private int stackLimit;

    private ContainerWand container;
}
