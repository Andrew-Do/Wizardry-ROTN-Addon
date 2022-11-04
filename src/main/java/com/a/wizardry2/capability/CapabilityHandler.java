package com.a.wizardry2.capability;

import com.a.wizardry2.Packet.PacketHandler;
import com.a.wizardry2.Packet.PacketManaSync;
import com.a.wizardry2.WizardryROTN;
import electroblob.wizardry.item.ItemWand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.config.Config;
import net.silentchaos512.scalinghealth.lib.EnumAreaDifficultyMode;
import net.silentchaos512.scalinghealth.network.NetworkHandler;
import net.silentchaos512.scalinghealth.network.message.MessageDataSync;
import net.silentchaos512.scalinghealth.network.message.MessageWorldDataSync;
import net.silentchaos512.scalinghealth.utils.SHPlayerDataHandler;
import net.silentchaos512.scalinghealth.world.ScalingHealthSavedData;

public class CapabilityHandler {
    public static final ResourceLocation MANA = new ResourceLocation(WizardryROTN.MODID, "mana");

    @SubscribeEvent
    public void attachMana(AttachCapabilitiesEvent<Entity> event)
    {
        if (!(event.getObject() instanceof EntityPlayer)) return;
        event.addCapability(MANA, new ManaProvider());
    }

    /**
     * Copy data from dead player to the new player
     */
    @SubscribeEvent
    public void onPlayerClone(Clone event)
    {
        EntityPlayer player = event.getEntityPlayer();
        IMana old = event.getOriginal().getCapability(ManaProvider.MANA_CAPABILITY, null);
        IMana current = player.getCapability(ManaProvider.MANA_CAPABILITY, null);

        //sync between clones on server
        current.sync(old.get(), old.getMax());
    }

    @SubscribeEvent
    public void onLivingUpdateEvent(LivingEvent.LivingUpdateEvent event) {
        if (!(event.getEntity() instanceof EntityPlayerMP)) return;

        EntityPlayerMP player = (EntityPlayerMP) event.getEntity();
        IMana current = player.getCapability(ManaProvider.MANA_CAPABILITY, null);

        if (current == null) return;

        boolean sync = false;
        if (current.isDirty()) sync = true;
        if (player.world.getTotalWorldTime() % 20 == 0 && current.get() != current.getMax())
        {
            current.add(3);
            sync = true;
        }
        if (sync) PacketHandler.net.sendTo(new PacketManaSync.Message(player, current.get(), current.getMax()), player);
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {

            IMana mana = event.player.getCapability(ManaProvider.MANA_CAPABILITY, null);
            if (mana != null)
                PacketHandler.net.sendTo(new PacketManaSync.Message(event.player, mana.get(), mana.getMax()), (EntityPlayerMP) event.player);
        }
    }

    @SubscribeEvent
    public void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.player instanceof EntityPlayerMP) {

            IMana mana = event.player.getCapability(ManaProvider.MANA_CAPABILITY, null);
            if (mana != null)
                PacketHandler.net.sendTo(new PacketManaSync.Message(event.player, mana.get(), mana.getMax()), (EntityPlayerMP) event.player);
        }
    }

    @SubscribeEvent
    public void onPlayerRespawnEvent(PlayerEvent.PlayerRespawnEvent event) {
        if (event.player instanceof EntityPlayerMP) {

            IMana mana = event.player.getCapability(ManaProvider.MANA_CAPABILITY, null);
            if (mana != null)
                PacketHandler.net.sendTo(new PacketManaSync.Message(event.player, mana.get(), mana.getMax()), (EntityPlayerMP) event.player);
        }
    }

    //WAND




}
