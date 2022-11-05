package com.a.wizardry2.container;

import com.a.wizardry2.capability.IWand;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.inventory.VirtualSlot;
import electroblob.wizardry.item.IWorkbenchItem;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.util.WandHelper;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class ContainerWand extends Container {
    public ItemStack wand;
    public EntityPlayer player;

    public ContainerWand(InventoryPlayer inventory, ItemStack wand) {
        //BackpackInfo backpackInfo = BackpackInfo.fromStack(backpackStack);
        //IItemHandler itemHandler = backpackInfo.getInventory();
        this.wand = wand;
        this.player = inventory.player;

        //SLOT      0-7     Book Slots
        //Slot      8       Crystal Slot
        //Slot      9       Centre Slot
        //Slot      10      Upgrade Slot

        IItemHandler itemHandler = (IItemHandler) wand.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        for(int i = 0; i < 8; i++){
            this.addSlotToContainer(new SlotWandItem(this, itemHandler, i, -999, -999, 1, ItemSpellBook.class));
        }

//        this.addSlotToContainer(new SlotWorkbenchItem(tileentity, CENTRE_SLOT, 80, 64, this));

        Set<Item> upgrades = new HashSet<>(WandHelper.getSpecialUpgrades()); // Can't be done statically.
        upgrades.add(WizardryItems.arcane_tome);
        upgrades.add(WizardryItems.resplendent_thread);
        upgrades.add(WizardryItems.crystal_silver_plating);
        upgrades.add(WizardryItems.ethereal_crystalweave);

        this.addSlotToContainer(new SlotWandItem(this, itemHandler, UPGRADE_SLOT, 147, 17, 1, upgrades.toArray(new Item[0])))
                .setBackgroundName(EMPTY_SLOT_UPGRADE.toString());

        //Render Player inventory
        for(int x = 0; x < 9; x++){
            Slot s = new Slot(inventory, x, 8 + x * 18, 196);
            if (s.getStack() != wand) this.addSlotToContainer(s);
        }

        for(int y = 0; y < 3; y++){
            for(int x = 0; x < 9; x++){
                this.addSlotToContainer(new Slot(inventory, 9 + x + y * 9, 8 + x * 18, 138 + y * 18));
            }
        }

        populateSpellSlots();

    }

    private GuiWand clientGui = null;

    public void setClientGui(GuiWand clientGui)
    {
        this.clientGui = clientGui;
    }


    public void onSlotChanged(int slotNumber, ItemStack stack, boolean playSound)
    {
        int spellSlots = ((IWorkbenchItem) this.wand.getItem()).getSpellSlotCount(this.wand);
        if (slotNumber < spellSlots)
        {
            if(MinecraftForge.EVENT_BUS.post(new SpellBindEvent(this.player, this))) return;
            if ( !((IWand) (wand.getItem())).onSlotChanged(slotNumber, wand, stack, this.player) ) return; //do the binding

            if (clientGui == null || !this.player.world.isRemote || !playSound) return; //client side check
            if (clientGui.initTicks > 0) return;
            Wizardry.proxy.playMovingSound(this.player, WizardrySounds.BLOCK_ARCANE_WORKBENCH_SPELLBIND, WizardrySounds.SPELLS, 1.0f, 1.0f, false);
            clientGui.animationTimer = 20;
        }
    }

    //TODO: Magic missile pierces enemies
    //TODO: look at ice shard code, and make fireball arc and aoe, fireball also needs to pass through tallgrass, look at magic missile code
    //TODO: ray spells slow whatever they are hitting


    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return player.getHeldItemMainhand().getItem() == this.wand.getItem();
    }

    //Helper

    public void populateSpellSlots()
    {
        int spellSlots = ((IWorkbenchItem) this.wand.getItem()).getSpellSlotCount(this.wand);
        int centreX = 80;
        int centreY = 64;

        for(int i = 0; i < spellSlots; i++)
        {
            int x = centreX + getBookSlotXOffset(i, spellSlots);
            int y = centreY + getBookSlotYOffset(i, spellSlots);
            showSlot(i, x, y);
        }

        // Hide the rest
        for(int i = spellSlots; i < UPGRADE_SLOT; i++)
        {
            hideSlot(i, player);
        }

    }

    /**
     * Shows the given slot in the container GUI at the given position. Intended to do the opposite of
     * {@link ContainerWand#hideSlot(int, EntityPlayer)}.
     * @param index The index of the slot to show.
     * @param x The x position to put the slot in.
     * @param y The y position to put the slot in.
     */
    private void showSlot(int index, int x, int y)
    {
        Slot slot = this.getSlot(index);
        slot.xPos = x;
        slot.yPos = y;
    }

    /**
     * Hides the given slot from the container GUI (moves it off the screen) and returns its contents to the given
     * player. If some or all of the items do not fit in the player's inventory, or if the player is null, they are
     * dropped on the floor.
     * @param index The index of the slot to hide.
     * @param player The player that is using this container.
     */
    private void hideSlot(int index, EntityPlayer player)
    {

        Slot slot = this.getSlot(index);

        // 'Removes' the slot from the container (moves it off the screen)
        slot.xPos = -999;
        slot.yPos = -999;

        ItemStack stack = slot.getStack();
        // This doesn't cause an infinite loop because slot i can never be a SlotWandArmour. In effect, it's
        // exactly the same as shift-clicking the slot, so why re-invent the wheel?
        ItemStack remainder = this.transferStackInSlot(player, index);

        if(remainder == ItemStack.EMPTY && stack != ItemStack.EMPTY)
        {
            slot.putStack(ItemStack.EMPTY);
            // The second parameter is never used...
            if(player != null) player.dropItem(stack, false);
        }
    }

    /** Returns the x offset (relative to the central slot) of the ith book slot when the total number of book slots is
     * equal to {@code bookSlotCount}. */
    public static int getBookSlotXOffset(int i, int bookSlotCount)
    {
        float angle = i * (2 * (float)Math.PI) / bookSlotCount;
        return Math.round(SLOT_RADIUS * MathHelper.sin(angle));
    }

    /** Returns the y offset (relative to the central slot) of the ith book slot when the total number of book slots is
     * equal to {@code bookSlotCount}. */
    public static int getBookSlotYOffset(int i, int bookSlotCount)
    {
        float angle = i * (2 * (float)Math.PI) / bookSlotCount;
        return Math.round(SLOT_RADIUS * -MathHelper.cos(angle)); // -cos because +y is downwards
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int clickedSlotId)
    {
        ItemStack remainder = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(clickedSlotId);

        if(slot != null && slot.getHasStack()){

            ItemStack stack = slot.getStack(); // The stack that was there originally
            remainder = stack.copy(); // A copy of that stack

            //Wand -> inventory, spellbooks
            if(clickedSlotId <= UPGRADE_SLOT
                    && !this.mergeItemStack(stack, UPGRADE_SLOT, UPGRADE_SLOT + PLAYER_INVENTORY_SIZE, true))
                return ItemStack.EMPTY;
            // Inventory -> wand
            else
            {
                int[] slotRange = findSlotRangeForItem(stack);

                // Try to move the stack into the workbench. If this fails...
                if(slotRange == null || !this.mergeItemStack(stack, slotRange[0], slotRange[1] + 1, false))
                    return ItemStack.EMPTY;
            }

            if(stack.getCount() == 0) slot.putStack(ItemStack.EMPTY);
            else slot.onSlotChanged();

            if(stack.getCount() == remainder.getCount()) return ItemStack.EMPTY;
            slot.onTake(player, stack);
        }

        return remainder;
    }

    /**
     * Returns the minimum and maximum IDs (inclusive) of the workbench slots that are appropriate for the given stack,
     * or null if no slots are appropriate. Note that this does not mean the stack <i>will</i> fit, only that it is valid
     * for all of the slots in the given range, and will fit if there is space for it.
     * @param stack The stack to find a slot for
     * @return A 2-element int array of the minimum and maximum slot IDs respectively
     */
    @Nullable
    private int[] findSlotRangeForItem(ItemStack stack)
    {

        if(this.getSlot(0).isItemValid(stack)) // Spell books
        {
            int spellSlots = ((IWorkbenchItem) wand.getItem()).getSpellSlotCount(wand);
            if(spellSlots > 0) return new int[]{0, spellSlots - 1};
        }
        else if(getSlot(UPGRADE_SLOT).isItemValid(stack)) return new int[]{UPGRADE_SLOT, UPGRADE_SLOT};

        return null; // It won't fit!
    }

    public static final ResourceLocation EMPTY_SLOT_UPGRADE = new ResourceLocation(Wizardry.MODID, "gui/container/empty_slot_upgrade");
    public static final int UPGRADE_SLOT = 8;
    public static final int PLAYER_INVENTORY_SIZE = 36;
    public static final int SLOT_RADIUS = 42;
}
