package com.a.wizardry2.potion;

import com.a.wizardry2.WizardryROTN;
import com.a.wizardry2.capability.IMana;
import com.a.wizardry2.capability.ManaProvider;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.potion.ICustomPotionParticles;
import electroblob.wizardry.potion.PotionMagicEffect;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.silentchaos512.scalinghealth.network.NetworkHandler;
import net.silentchaos512.scalinghealth.network.message.MessageDataSync;
import net.silentchaos512.scalinghealth.utils.SHPlayerDataHandler;

public class PotionBurnLife extends PotionMagicEffect implements ICustomPotionParticles {

    public PotionBurnLife(boolean isBadEffect, int liquidColour, ResourceLocation texture) {
        super(isBadEffect, liquidColour, new ResourceLocation(Wizardry.MODID, "textures/gui/potion_icons/frost_step.png"));
        this.setPotionName("potion." + WizardryROTN.MODID + ":burn_life");
    }

    @Override
    public void spawnCustomParticle(World world, double x, double y, double z){
        ParticleBuilder.create(ParticleBuilder.Type.SNOW).pos(x, y, z).time(15 + world.rand.nextInt(5)).spawn(world);
    }

    @Override
    public void performEffect(EntityLivingBase host, int strength)
    {
        //if client side return
        if (host.world.isRemote) return;

        IMana mana = host.getCapability(ManaProvider.MANA_CAPABILITY, null);
        if (mana == null) return;

        mana.addMax(100);
        mana.add(100);

        if (host instanceof EntityPlayer)
        {
            SHPlayerDataHandler.PlayerData data = SHPlayerDataHandler.get((EntityPlayer) host);
            if (data == null) return;
            data.incrementMaxHealth(-1);
            NetworkHandler.INSTANCE.sendTo(new MessageDataSync(data, (EntityPlayer) host), (EntityPlayerMP) host);
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier){
        return duration == 1;
    }

    //TODO: create config file
    //TODO: use mixin, modify heart container item to check config file for limit of uses
        //ItemHeartContainer
        //TODO: Add config option
    //TODO: test timing


}
