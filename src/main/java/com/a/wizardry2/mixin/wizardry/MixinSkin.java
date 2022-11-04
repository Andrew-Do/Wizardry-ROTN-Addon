package com.a.wizardry2.mixin.wizardry;

import electroblob.wizardry.client.ClientProxy;
import electroblob.wizardry.client.DrawingUtils;
import electroblob.wizardry.client.gui.GuiSpellDisplay.Skin;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Skin.class)
public class MixinSkin {

    @Overwrite(remap = false)
    public void drawText(int x, int y, boolean flipX, boolean flipY, String prevSpellName, String spellName, String nextSpellName, float animationProgress){
        IMixinSkin o = ((IMixinSkin)(Object)this);

        // Moves the origin if the HUD does not mirror; neatens the rest of the code.
        if(flipX && !o.get_mirrorX()) x -= o.get_width();
        if(flipY && !o.get_mirrorY()) y += o.get_height();

        FontRenderer font = ClientProxy.mixedFontRenderer; // On this occasion we're client-side so this is OK

        // Position of the selected spell name in normal display, also used for interpolation when animating
        int x1 = flipX && o.get_mirrorX() ? x - o.get_width() : x + o.get_textInsetX();
        // The text is an odd number of pixels high, so we need to subtract an extra 1 when not flipped
        int y1 = flipY && o.get_mirrorY() ? y + o.get_textInsetY() - font.FONT_HEIGHT/2 + 2 : y - o.get_textInsetY() - font.FONT_HEIGHT/2 - 1;

        int maxWidth = o.get_width() - o.get_textInsetX(); // Maximum width of the text

        if(animationProgress == 0){ // Normal display



            DrawingUtils.drawScaledStringToWidth(font, spellName, x1, y1, 1, 0xffffffff, maxWidth, true, flipX && o.get_mirrorX());

        }

    }

}
