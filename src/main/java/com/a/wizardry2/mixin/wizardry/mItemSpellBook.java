package com.a.wizardry2.mixin.wizardry;

import electroblob.wizardry.Wizardry;
import electroblob.wizardry.WizardryGuiHandler;
import electroblob.wizardry.data.SpellGlyphData;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.registry.WizardryTabs;
import electroblob.wizardry.spell.Spell;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.List;

@Mixin(ItemSpellBook.class)
public abstract class mItemSpellBook extends Item {

    @Overwrite(remap = false)
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand){
        return ActionResult.newResult(EnumActionResult.PASS, player.getHeldItem(hand));
    }

    // This is accessed during loading (before we even get to the main menu) for search tree population
    // Obviously the world is always null at that point, because no world objects exist! However, outside of a world
    // there are no guarantees as to spell metadata order so we just have to give up (and we can't account for discovery)
    // TODO: Search trees seem to get reloaded when the mappings change so in theory this should work ok, why doesn't it?
    @Overwrite(remap = false)
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemstack, World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced){

        if(world == null) world = Wizardry.proxy.getTheWorld(); // But... I need the world!

        // Tooltip is left blank for wizards buying generic spell books.
        if(world != null && itemstack.getItemDamage() != OreDictionary.WILDCARD_VALUE){

            Spell spell = Spell.byMetadata(itemstack.getItemDamage());

            // Element colour is not given for undiscovered spells
            tooltip.add("\u00A77" + spell.getDisplayNameWithFormatting());
            tooltip.add(spell.getTier().getDisplayNameWithFormatting());
            tooltip.add(spell.getElement().getDisplayName());
            tooltip.add(spell.getType().getDisplayName());

//            // Advanced tooltips displays the source mod's name if the spell is not from Wizardry
//            if (advanced.isAdvanced() && this.getRegistryName().toString().equals(Wizardry.MODID + ":spell_book") && !spell.getRegistryName().getResourceDomain().equals(Wizardry.MODID)) {
//                String modId = spell.getRegistryName().getResourceDomain();
//                String name = new Style().setColor(TextFormatting.BLUE).setItalic(true).getFormattingCode() +
//                        Loader.instance().getIndexedModList().get(modId).getMetadata().name;
//                tooltip.add(name);
//            }
        }
    }

    //Helper

    @Overwrite(remap = false)
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list){

        if(tab == WizardryTabs.SPELLS){

            List<Spell> spells = Spell.getAllSpells();
            spells.removeIf(s -> !s.applicableForItem(this));

            for(Spell spell : spells){
                if (spell.isEnabled()) list.add(new ItemStack(this, 1, spell.metadata()));
            }
        }
    }

    //TODO: generate tooltip display

    //TODO: generate tooltip display
    //TODO: if no properties are generated, right click to generate and drop book
    //TODO: Color tiers based on

    //TODO: generate nbt based on spell properties and attach to book

    //TODO: craft books together to combine the best properties
}
