package com.a.wizardry2.mixin.wizardry;

import electroblob.wizardry.client.gui.GuiSpellDisplay;
import org.spongepowered.asm.mixin.Mixin;
import electroblob.wizardry.client.gui.GuiSpellDisplay.Skin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Skin.class)
public interface IMixinSkin {
    @Accessor("mirrorX")
    boolean get_mirrorX();

    @Accessor("mirrorY")
    boolean get_mirrorY();

    @Accessor("width")
    int get_width();

    @Accessor("height")
    int get_height();

    @Accessor("textInsetX")
    int get_textInsetX();

    @Accessor("textInsetY")
    int get_textInsetY();

    @Accessor("cascadeOffsetX")
    int get_cascadeOffsetX();

    @Accessor("cascadeOffsetY")
    int get_cascadeOffsetY();




}
