package com.a.wizardry2.mixin.betterhud;

import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.MathUtil;
import jobicade.betterhud.util.bars.StatBarBasic;
import jobicade.betterhud.util.bars.StatBarFood;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

@Mixin(StatBarFood.class)
public abstract class mStatBarFood extends StatBarBasic<EntityPlayer> {

    @Override
    protected void drawIcon(int i, Rect bounds, Direction contentAlignment) {
        bounds = bounds.translate(0, getIconBounce(i));

        for(Rect texture : getIcons(i)) {
            if(texture != null) {
                texture = ensureNative(texture, contentAlignment.withRow(1));
                float scale = 0.75F;
                GlStateManager.pushMatrix();
                GlStateManager.scale(scale, scale, 1F);
                GlUtil.drawRect(bounds, texture);
                GlStateManager.popMatrix();
            }
        }
    }

}
