package com.a.wizardry2.potion;

import com.a.wizardry2.WizardryROTN;
import com.a.wizardry2.capability.IMana;
import com.a.wizardry2.capability.ManaProvider;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.potion.ICustomPotionParticles;
import electroblob.wizardry.potion.PotionMagicEffect;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class PotionRestoreMana extends PotionMagicEffect implements ICustomPotionParticles {
    public PotionRestoreMana(boolean isBadEffect, int liquidColour, ResourceLocation texture) {
        super(isBadEffect, liquidColour, new ResourceLocation(Wizardry.MODID, "textures/gui/potion_icons/frost_step.png"));
        this.setPotionName("potion." + WizardryROTN.MODID + ":restore_mana");
    }

    @Override
    public void spawnCustomParticle(World world, double x, double y, double z){
        ParticleBuilder.create(ParticleBuilder.Type.SNOW).pos(x, y, z).time(15 + world.rand.nextInt(5)).spawn(world);
    }

    @Override
    public void performEffect(EntityLivingBase host, int strength)
    {
        //each tier adds 0.25 mana restore
        int amount = strength * 3;
        IMana mana = host.getCapability(ManaProvider.MANA_CAPABILITY, null);
        if (mana != null) mana.add(amount);
    }

    @Override
    public boolean isReady(int duration, int amplifier){
        return duration % 19 == 0;
    }
}

//TODO: test timing