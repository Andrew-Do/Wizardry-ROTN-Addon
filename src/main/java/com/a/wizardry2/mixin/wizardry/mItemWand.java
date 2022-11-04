package com.a.wizardry2.mixin.wizardry;

import com.a.wizardry2.WizardryROTN;
import com.a.wizardry2.capability.IWand;
import com.a.wizardry2.capability.ManaProvider;
import com.a.wizardry2.capability.WandProvider;
import com.a.wizardry2.container.ContainerWand;
import com.a.wizardry2.container.GuiHandler;
import com.google.common.collect.Multimap;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.item.ItemWand;
import electroblob.wizardry.packet.PacketCastSpell;
import electroblob.wizardry.packet.WizardryPacketHandler;
import electroblob.wizardry.registry.*;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.SpellProperties;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@Mixin(ItemWand.class)
public abstract class mItemWand extends Item implements ISpellCastingItem, IWand {

    //Storage Capability Implementation

    @Nonnull
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new WandProvider(stack);
    }

    @Overwrite(remap = false)
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){

        ItemStack stack = player.getHeldItem(hand);

        // Alternate right-click function; overrides spell casting.
        if(this.selectMinionTarget(player, world)) return new ActionResult<>(EnumActionResult.SUCCESS, stack);

        if (player.isSneaking())
        {
            //TODO: Open Test GUI Here
            player.openGui(WizardryROTN.instance, GuiHandler.OPEN_GUI_WAND_ID, world, hand == EnumHand.OFF_HAND ? 1 : 0, 0, 0);
            return new ActionResult(EnumActionResult.SUCCESS, stack);
        }

        Spell spell = WandHelper.getCurrentSpell(stack);
        SpellModifiers modifiers = this.calculateModifiers(stack, player, spell);

        if(canCast(stack, spell, player, hand, 0, modifiers)){
            // Need to account for the modifier since it could be zero even if the original charge-up wasn't
            int chargeup = (int)(spell.getChargeup() * modifiers.get(SpellModifiers.CHARGEUP));

            if(spell.isContinuous || chargeup > 0){
                // Spells that need the mouse to be held (continuous, charge-up or both)
                if(!player.isHandActive()){
                    player.setActiveHand(hand);
                    // Store the modifiers for use later
                    if(WizardData.get(player) != null) WizardData.get(player).itemCastingModifiers = modifiers;
                    if(chargeup > 0 && world.isRemote) Wizardry.proxy.playChargeupSound(player);
                    return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                }
            }else{
                // All other (instant) spells
                if(cast(stack, spell, player, hand, 0, modifiers)){
                    return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                }
            }
        }

        return new ActionResult<>(EnumActionResult.FAIL, stack);
    }

    @Shadow private boolean selectMinionTarget(EntityPlayer player, World world) {throw new AbstractMethodError("Shadow");}
    @Shadow public abstract SpellModifiers calculateModifiers(ItemStack stack, EntityPlayer player, Spell spell);

    //@Inject(at = @At("TAIL"), method = "<init>()V")
    //private void init(Tier tier, Element element, CallbackInfo info){
    //
    //}


    //Casting Logic
    @Overwrite(remap = false)
    public boolean canCast(ItemStack stack, Spell spell, EntityPlayer caster, EnumHand hand, int castingTick, SpellModifiers modifiers){

        // Spells can only be cast if the casting events aren't cancelled...
        if(castingTick == 0){
            if(MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Pre(SpellCastEvent.Source.WAND, spell, caster, modifiers))) return false;
        }else{
            if(MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Tick(SpellCastEvent.Source.WAND, spell, caster, modifiers, castingTick))) return false;
        }

        int cost = (int)(spell.getCost() * modifiers.get(SpellModifiers.COST) + 0.1f); // Weird floaty rounding

        // As of wizardry 4.2 mana cost is only divided over two intervals each second
        if(spell.isContinuous) cost = getDistributedCost(cost, castingTick);


        // ...and the wand has enough mana to cast the spell...
        return cost <= caster.getCapability(ManaProvider.MANA_CAPABILITY, null).get() // This comes first because it changes over time
                // ...and the wand is the same tier as the spell or higher...
                && spell.getTier().level <= this.tier.level
                // ...and either the spell is not in cooldown or the player is in creative mode
                && (WandHelper.getCurrentCooldown(stack) == 0 || caster.isCreative());
    }

    @Overwrite(remap = false)
    public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack){
        // Ignore durability changes
        if(ItemStack.areItemsEqualIgnoreDurability(oldStack, newStack)) return true;
        return super.canContinueUsing(oldStack, newStack);
    }

    @Overwrite(remap = false)
    public boolean cast(ItemStack stack, Spell spell, EntityPlayer caster, EnumHand hand, int castingTick, SpellModifiers modifiers){

        World world = caster.world;

        if(world.isRemote && !spell.isContinuous && spell.requiresPacket()) return false;

        if(spell.cast(world, caster, hand, castingTick, modifiers)){

            if(castingTick == 0) MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Post(SpellCastEvent.Source.WAND, spell, caster, modifiers));

            if(!world.isRemote){

                // Continuous spells never require packets so don't rely on the requiresPacket method to specify it
                if(!spell.isContinuous && spell.requiresPacket()){
                    // Sends a packet to all players in dimension to tell them to spawn particles.
                    IMessage msg = new PacketCastSpell.Message(caster.getEntityId(), hand, spell, modifiers);
                    WizardryPacketHandler.net.sendToDimension(msg, world.provider.getDimension());
                }

                // Mana cost
                int cost = (int)(spell.getCost() * modifiers.get(SpellModifiers.COST) + 0.1f); // Weird floaty rounding
                // As of wizardry 4.2 mana cost is only divided over two intervals each second
                if(spell.isContinuous) cost = getDistributedCost(cost, castingTick);

                if (cost > 0) caster.getCapability(ManaProvider.MANA_CAPABILITY, null).add(-cost);
            }

            caster.setActiveHand(hand);

            // Cooldown
            if(!spell.isContinuous && !caster.isCreative()){ // Spells only have a cooldown in survival
                WandHelper.setCurrentCooldown(stack, (int)(spell.getCooldown() * modifiers.get(WizardryItems.cooldown_upgrade)));
            }

            // Progression
            if(this.tier.level < Tier.MASTER.level && castingTick % 20 == 0){

                // We don't care about cost modifiers here, otherwise players would be penalised for wearing robes!
                int progression = (int)(spell.getCost() * modifiers.get(SpellModifiers.PROGRESSION));
                WandHelper.addProgression(stack, progression);

                if(!Wizardry.settings.legacyWandLevelling){ // Don't display the message if legacy wand levelling is enabled
                    // If the wand just gained enough progression to be upgraded...
                    Tier nextTier = tier.next();
                    int excess = WandHelper.getProgression(stack) - nextTier.getProgression();
                    if(excess >= 0 && excess < progression){
                        // ...display a message above the player's hotbar
                        caster.playSound(WizardrySounds.ITEM_WAND_LEVELUP, 1.25f, 1);
                        WizardryAdvancementTriggers.wand_levelup.triggerFor(caster);
                        if(!world.isRemote)
                            caster.sendMessage(new TextComponentTranslation("item." + Wizardry.MODID + ":wand.levelup",
                                    this.getItemStackDisplayName(stack), nextTier.getNameForTranslationFormatted()));
                    }
                }

                WizardData.get(caster).trackRecentSpell(spell);
            }

            return true;
        }

        return false;
    }

    @Overwrite(remap = false)
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isHeld){
        WandHelper.decrementCooldowns(stack);
        //// Decrements wand damage (increases mana) every 1.5 seconds if it has a condenser upgrade
        //if(!world.isRemote && !this.isManaFull(stack) && world.getTotalWorldTime() % Constants.CONDENSER_TICK_INTERVAL == 0){
        //    // If the upgrade level is 0, this does nothing anyway.
        //    this.rechargeMana(stack, WandHelper.getUpgradeLevel(stack, WizardryItems.condenser_upgrade));
        //}
    }

    //Spell Memorization

    public boolean onSlotChanged(int slotNumber, ItemStack wand, ItemStack item, EntityPlayer player)
    {
        if (slotNumber < ((ItemWand) wand.getItem()).getSpellSlotCount(wand)) //if it's a spellbook slot
        {
            Spell[] spells = WandHelper.getSpells(wand);
            if(spells.length <= 0) spells = new Spell[ItemWand.BASE_SPELL_SLOTS];
            Spell s = Spell.byMetadata(item.getItemDamage());
            if (s == Spells.none
                    || (!(s.getTier().level > this.tier.level) && s.isEnabled(SpellProperties.Context.WANDS)) ) //if you removed a spellbook
            {
                spells[slotNumber] = s;
                WandHelper.setSpells(wand, spells);
                return true;
            }
            return false;
        }

        if (slotNumber == ContainerWand.UPGRADE_SLOT)
        {
            //centre.putStack(this.applyUpgrade(player, centre.getStack(), upgrade.getStack()));
        }
        return true;
    }


    //Tooltip Information

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, World world, List<String> text, net.minecraft.client.util.ITooltipFlag advanced){

        EntityPlayer player = net.minecraft.client.Minecraft.getMinecraft().player;
        if (player == null) { return; }
        // +0.5f is necessary due to the error in the way floats are calculated.
        if(element != null) text.add(Wizardry.proxy.translate("item." + Wizardry.MODID + ":wand.buff",
                new Style().setColor(TextFormatting.DARK_GRAY),
                (int)((tier.level + 1) * Constants.POTENCY_INCREASE_PER_TIER * 100 + 0.5f), element.getDisplayName()));

        text.add(Wizardry.proxy.translate("item." + Wizardry.MODID + ":wand.progression", new Style().setColor(TextFormatting.GRAY),
                WandHelper.getProgression(stack), this.tier.level < Tier.MASTER.level ? tier.next().getProgression() : 0));
    }

    //Shadows and Disabled Methods

    @Shadow protected static int getDistributedCost(int cost, int castingTick){ throw new AbstractMethodError("Shadow");}
    @Overwrite(remap = false) public static void onAttackEntityEvent(AttackEntityEvent event){}
    @Overwrite(remap = false) public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase wielder){return true;}
    @Overwrite(remap = false) public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack){return super.getAttributeModifiers(slot, stack);}
    @Overwrite(remap = false) public boolean onApplyButtonPressed(EntityPlayer player, Slot centre, Slot crystals, Slot upgrade, Slot[] spellBooks) { return false; }
    @Shadow public Tier tier;
    @Shadow public Element element;

}
