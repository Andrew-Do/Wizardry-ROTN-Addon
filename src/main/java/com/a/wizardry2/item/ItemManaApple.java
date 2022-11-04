package com.a.wizardry2.item;

import com.a.wizardry2.registry.Potions;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemManaApple extends ItemFood {
    public ItemManaApple() {
        super(2, 2, false);
    }

    protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player)
    {
        if (!worldIn.isRemote)
        {
            player.addPotionEffect(new PotionEffect(Potions.burn_life, 160, 0));
        }


    }



}
