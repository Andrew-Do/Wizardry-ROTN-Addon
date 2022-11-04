package com.a.wizardry2.mixin.betterhud;

import com.a.wizardry2.Util;
import com.a.wizardry2.capability.IMana;
import com.a.wizardry2.capability.ManaProvider;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.render.Label;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.MathUtil;
import jobicade.betterhud.util.bars.StatBar;
import jobicade.betterhud.util.bars.StatBarHealth;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(StatBarHealth.class)
public abstract class mStatBarHealth extends StatBar<EntityLivingBase> {

    public Color BLOOD_RED = new Color(136, 8, 8);
    public Color GOLD = new Color(249, 242, 149);
    public Color AZURE = new Color(0, 102, 255);

    @Overwrite(remap = false)
    public void render() {
        if(!DirectionOptions.CORNERS.isValid(contentAlignment)) {
            throw new IllegalArgumentException("Bar must start in a corner");
        }

        //Display Health

        double maxHealth = MathUtil.getHealthForDisplay(host.getMaxHealth());
        double health = host.getHealth();
        double absorptionHealth = MathUtil.getHealthForDisplay(host.getAbsorptionAmount());

        setBounds(new Rect(bounds.getX(), bounds.getY(), Util.getWidth(health, maxHealth), 4));
        GlUtil.drawRect(bounds, BLOOD_RED);

        setBounds(new Rect(bounds.getX(), bounds.getY(), Util.getWidth(absorptionHealth, maxHealth), 4));
        GlUtil.drawRect(bounds, GOLD);

        String healthText = ((int)(health + absorptionHealth)) + "/" + ((int) maxHealth);
        drawString(healthText, bounds.getX(), bounds.getY(), Util.hex2Color("#FFFFFF").colorToText());

        //Display Mana

        IMana current = host.getCapability(ManaProvider.MANA_CAPABILITY, null);

        float maxMana = current.getMax() / 100F;
        float mana = current.get()  / 100F;

        setBounds(new Rect(bounds.getX(), bounds.getY() + 4, Util.getWidth(mana, maxMana), 4));
        if (maxMana > 0) GlUtil.drawRect(bounds, AZURE);

        String s = (mana % 1.0 == 0) ? String.format("%s", (long) mana) : String.format("%.2f", mana);
        drawString(s + "/" + ((int) maxMana), bounds.getX(), bounds.getY() + 4, Util.hex2Color("#FFFFFF").colorToText());
    }

    public void drawString(String string, int xOffset, int yOffset, int color) {
        float scale = 0.5F;
        GlStateManager.pushMatrix();
        GlStateManager.scale(scale, scale, 1F);
        Minecraft.getMinecraft().fontRenderer.drawString(string, xOffset+10, yOffset + 5, color, true);
        GlStateManager.popMatrix();
    }

}
