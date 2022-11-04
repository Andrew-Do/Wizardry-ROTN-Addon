package com.a.wizardry2.mixin.scalinghealth;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.silentchaos512.scalinghealth.client.HeartDisplayHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(HeartDisplayHandler.class)
public class RemoveHealthBar {

    //remove scaling health's bar display
    @Overwrite(remap = false)
    public void onHealthBar(RenderGameOverlayEvent.Pre event) {
    }
}
