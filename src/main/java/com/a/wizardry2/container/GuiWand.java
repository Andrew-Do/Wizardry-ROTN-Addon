package com.a.wizardry2.container;

import com.a.wizardry2.WizardryROTN;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.client.DrawingUtils;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.data.SpellGlyphData;
import electroblob.wizardry.item.*;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(Side.CLIENT)
public class GuiWand extends GuiContainer {

    private ContainerWand wandContainer;

    public GuiWand(ContainerWand container){
        super(container);
        this.wandContainer = (ContainerWand) inventorySlots;
        xSize = MAIN_GUI_WIDTH;
        ySize = 220;
    }

    @Override
    public void initGui()
    {

        this.mc.player.openContainer = this.inventorySlots;
        this.guiLeft = (this.width - MAIN_GUI_WIDTH) / 2; // Use MAIN_GUI_WIDTH, not xSize, otherwise JEI messes with it
        this.guiTop = (this.height - this.ySize) / 2;

        Keyboard.enableRepeatEvents(true);

        this.buttonList.clear();

        this.tooltipElements.clear();
        this.tooltipElements.add(new GuiWand.TooltipElementItemName(new Style().setColor(TextFormatting.WHITE), LINE_SPACING_WIDE));
        this.tooltipElements.add(new GuiWand.TooltipElementSpellList(LINE_SPACING_WIDE));
        this.tooltipElements.add(new GuiWand.TooltipElementUpgradeList(LINE_SPACING_WIDE));

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        GlStateManager.color(1, 1, 1, 1); // Just in case

        xSize = MAIN_GUI_WIDTH + TOOLTIP_WIDTH;
        guiLeft = (this.width - MAIN_GUI_WIDTH) / 2;

        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1, 1, 1, 1);
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);

        // Grey background
        DrawingUtils.drawTexturedRect(guiLeft + RUNE_LEFT, guiTop + RUNE_TOP, MAIN_GUI_WIDTH + TOOLTIP_WIDTH, 0,
                RUNE_WIDTH, RUNE_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);

        // Yellow 'halo'
        if(animationTimer > 0){
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            int x = guiLeft + RUNE_LEFT + RUNE_WIDTH/2;
            int y = guiTop + RUNE_TOP + RUNE_HEIGHT/2;

            float scale = (animationTimer + partialTicks)/ANIMATION_DURATION;
            scale = (float)(1 - Math.pow(1-scale, 1.4f)); // Makes it slower at the start and speed up
            GlStateManager.scale(scale, scale, 1);
            GlStateManager.translate(x/scale, y/scale, 0);

            DrawingUtils.drawTexturedRect(-HALO_DIAMETER /2, -HALO_DIAMETER /2, MAIN_GUI_WIDTH + TOOLTIP_WIDTH, RUNE_HEIGHT,
                    HALO_DIAMETER, HALO_DIAMETER, TEXTURE_WIDTH, TEXTURE_HEIGHT);

            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }

        // Main inventory
        DrawingUtils.drawTexturedRect(guiLeft, guiTop, 0, 0, MAIN_GUI_WIDTH, ySize, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        float opacity = (animationTimer + partialTicks)/ANIMATION_DURATION;

        // Spell book slots (always use guiLeft and guiTop here regardless of bookshelf UI visibility
        for(int i = 0; i < ContainerWand.UPGRADE_SLOT; i++){

            Slot slot = this.inventorySlots.getSlot(i);

            if(slot.xPos >= 0 && slot.yPos >= 0){
                // Slot background
                DrawingUtils.drawTexturedRect(guiLeft + slot.xPos - 10, guiTop + slot.yPos - 10, 0, 220, 36, 36, TEXTURE_WIDTH, TEXTURE_HEIGHT);

                // Slot animation
                if(animationTimer > 0 && slot.getHasStack()){
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                    GlStateManager.color(1, 1, 1, opacity);

                    DrawingUtils.drawTexturedRect(guiLeft + slot.xPos - 10, guiTop + slot.yPos - 10, 36, 220, 36, 36, TEXTURE_WIDTH, TEXTURE_HEIGHT);

                    GlStateManager.color(1, 1, 1, 1);
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();
                }
            }
        }

        //upgrade slot animations
        if(animationTimer > 0){
            Slot upgrades = this.inventorySlots.getSlot(ContainerWand.UPGRADE_SLOT);

            if(upgrades.getHasStack()){

                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                GlStateManager.color(1, 1, 1, opacity);

                DrawingUtils.drawTexturedRect(guiLeft + upgrades.xPos - 8, guiTop + upgrades.yPos - 8,
                        MAIN_GUI_WIDTH + TOOLTIP_WIDTH + RUNE_WIDTH, 0, 32, 32, TEXTURE_WIDTH, TEXTURE_HEIGHT);

                GlStateManager.color(1, 1, 1, 1);
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }

        // Tooltip
        int tooltipHeight = tooltipElements.stream().mapToInt(e -> e.getTotalHeight(wandContainer.wand)).sum()
                - tooltipElements.get(tooltipElements.size() - 1).spaceAfter; // Remove space after last element

        // Tooltip box
        DrawingUtils.drawTexturedRect(guiLeft + MAIN_GUI_WIDTH, guiTop, MAIN_GUI_WIDTH, 0, TOOLTIP_WIDTH,
                TOOLTIP_BORDER + tooltipHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        DrawingUtils.drawTexturedRect(guiLeft + MAIN_GUI_WIDTH, guiTop + TOOLTIP_BORDER + tooltipHeight,
                MAIN_GUI_WIDTH, ySize - TOOLTIP_BORDER, TOOLTIP_WIDTH, TOOLTIP_BORDER, TEXTURE_WIDTH, TEXTURE_HEIGHT);

        int x = guiLeft + MAIN_GUI_WIDTH + TOOLTIP_BORDER;
        int y = guiTop + TOOLTIP_BORDER;

        for(GuiWand.TooltipElement element : this.tooltipElements){
            y = element.drawBackgroundLayer(x, y, wandContainer.wand, partialTicks, mouseX, mouseY);
        }

        GlStateManager.color(1, 1, 1, 1);
        Minecraft.getMinecraft().renderEngine.bindTexture(texture);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        GlStateManager.color(1, 1, 1, 1); // Just in case

        int x = MAIN_GUI_WIDTH + TOOLTIP_BORDER;
        int y = TOOLTIP_BORDER;

        for(GuiWand.TooltipElement element : this.tooltipElements){
            y = element.drawForegroundLayer(x, y, wandContainer.wand, mouseX, mouseY);
        }

    }

    //Helper
    public int initTicks = 20;

    @Override public void updateScreen()
    {
        if(animationTimer > 0) animationTimer--;
        if(initTicks > 0) initTicks--;
    }

    @Override
    public void onGuiClosed(){
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }

    // Controls

    @Override
    protected void actionPerformed(GuiButton button){
        if(!button.enabled) return;

//        if(button == applyBtn){
//            // Packet building
//            IMessage msg = new PacketControlInput.Message(PacketControlInput.ControlType.APPLY_BUTTON);
//            WizardryPacketHandler.net.sendToServer(msg);
//
//        }
    }

    @SubscribeEvent
    public static void onTextureStitchEvent(TextureStitchEvent.Pre event){
        event.getMap().registerSprite(ContainerWand.EMPTY_SLOT_UPGRADE);
    }

    private abstract static class TooltipElement {

        private final GuiWand.TooltipElement[] children;
        private final int spaceAfter;

        public TooltipElement(int spaceAfter, GuiWand.TooltipElement... children){
            this.children = children;
            this.spaceAfter = spaceAfter;
        }

        // Externally-called methods

        /**
         * Returns the height of this tooltip element and its children, with spacing, or 0 if it is not visible.
         * @param stack The item stack currently in the central slot of the workbench
         * @return The total height of this tooltip element and its children, including spacing
         */
        public int getTotalHeight(ItemStack stack){
            if(!this.isVisible(stack)) return 0;
            int height = this.getHeight(stack);
            for(GuiWand.TooltipElement child : children) height += child.getTotalHeight(stack);
            return height + spaceAfter;
        }

        /**
         * Draws the background layer of this tooltip element and all of its children.
         * @param x The x-coordinate of the top left corner of this element
         * @param y The y-coordinate of the top left corner of this element
         * @param stack The item stack currently in the central slot of the workbench
         * @param partialTicks The current partial tick time
         * @param mouseX The current x-coordinate of the cursor
         * @param mouseY The current y-coordinate of the cursor
         * @return The y-coordinate at which the next tooltip element should start
         */
        public int drawBackgroundLayer(int x, int y, ItemStack stack, float partialTicks, int mouseX, int mouseY){
            if(!this.isVisible(stack)) return y;
            this.drawBackground(x, y, stack, partialTicks, mouseX, mouseY);
            y += this.getHeight(stack);
            for(GuiWand.TooltipElement child : children) y = child.drawBackgroundLayer(x, y, stack, partialTicks, mouseX, mouseY);
            return y + spaceAfter;
        }

        /**
         * Draws the foreground layer of this tooltip element and all of its children.
         * @param x The x-coordinate of the top left corner of this element
         * @param y The y-coordinate of the top left corner of this element
         * @param stack The item stack currently in the central slot of the workbench
         * @param mouseX The current x-coordinate of the cursor
         * @param mouseY The current y-coordinate of the cursor
         * @return The y-coordinate at which the next tooltip element should start
         */
        public int drawForegroundLayer(int x, int y, ItemStack stack, int mouseX, int mouseY){
            if(!this.isVisible(stack)) return y;
            this.drawForeground(x, y, stack, mouseX, mouseY);
            y += this.getHeight(stack);
            for(GuiWand.TooltipElement child : children) y = child.drawForegroundLayer(x, y, stack, mouseX, mouseY);
            return y + spaceAfter;
        }

        // Abstract internal methods

        /**
         * Returns whether this tooltip element should be shown.
         * @param stack The item stack currently in the central slot of the workbench
         * @return True if this element should be shown, false if not.
         */
        protected abstract boolean isVisible(ItemStack stack);

        /**
         * Returns the height of this tooltip element. This is for internal implementation.
         * @param stack The item stack currently in the central slot of the workbench
         * @return The height of this tooltip element, excluding children and spacing.
         */
        protected abstract int getHeight(ItemStack stack);

        /**
         * Draws the background layer of this tooltip element (excluding children). This is for internal implementation.
         * @param x The x-coordinate of the top left corner of this element
         * @param y The y-coordinate of the top left corner of this element
         * @param stack The item stack currently in the central slot of the workbench
         * @param partialTicks The current partial tick time
         * @param mouseX The current x-coordinate of the cursor
         * @param mouseY The current y-coordinate of the cursor
         */
        protected abstract void drawBackground(int x, int y, ItemStack stack, float partialTicks, int mouseX, int mouseY);

        /**
         * Draws the foreground layer of this tooltip element (excluding children). This is for internal implementation.
         * @param x The x-coordinate of the top left corner of this element
         * @param y The y-coordinate of the top left corner of this element
         * @param stack The item stack currently in the central slot of the workbench
         * @param mouseX The current x-coordinate of the cursor
         * @param mouseY The current y-coordinate of the cursor
         */
        protected abstract void drawForeground(int x, int y, ItemStack stack, int mouseX, int mouseY);

    }

    private class TooltipElementText extends GuiWand.TooltipElement {

        private final String text; // Can't change the language whilst in a GUI so we can just store the translated text
        private final Style style;

        public TooltipElementText(String text, Style style, int spaceAfter, GuiWand.TooltipElement... children){
            super(spaceAfter, children);
            this.text = text;
            this.style = style;
        }

        /** Returns the text for this element. */
        protected String getText(ItemStack stack){
            return text;
        }

        protected FontRenderer getFontRenderer(ItemStack stack){
            return fontRenderer;
        }

        protected int getColour(ItemStack stack){
            return 0;
        }

        @Override
        protected boolean isVisible(ItemStack stack){
            return true; // Always visible by default
        }

        @Override
        protected int getHeight(ItemStack stack){
            return getFontRenderer(stack).listFormattedStringToWidth(getText(stack), TOOLTIP_WIDTH - 2 * TOOLTIP_BORDER)
                    .size() * getFontRenderer(stack).FONT_HEIGHT;
        }

        @Override
        protected void drawBackground(int x, int y, ItemStack stack, float partialTicks, int mouseX, int mouseY){
            // Nothing here because this element is only text!
        }

        @Override
        protected void drawForeground(int x, int y, ItemStack stack, int mouseX, int mouseY){
            for(String line : getFontRenderer(stack).listFormattedStringToWidth(getText(stack), TOOLTIP_WIDTH - 2 * TOOLTIP_BORDER)){
                getFontRenderer(stack).drawStringWithShadow(style.getFormattingCode() + line, x, y, getColour(stack));
                y += getFontRenderer(stack).FONT_HEIGHT;
            }
        }
    }

    private class TooltipElementItemName extends GuiWand.TooltipElementText {

        public TooltipElementItemName(Style style, int spaceAfter){
            super(null, style, spaceAfter);
        }

        @Override
        protected String getText(ItemStack stack){
            return stack.getDisplayName();
        }

    }

    private class TooltipElementSpellList extends GuiWand.TooltipElement {

        public TooltipElementSpellList(int spaceAfter){
            super(spaceAfter, generateSpellEntries(8));
        }

        @Override
        protected boolean isVisible(ItemStack stack){
            return stack.getItem() instanceof ISpellCastingItem && ((ISpellCastingItem)stack.getItem()).showSpellsInWorkbench(mc.player, stack);
        }

        @Override
        protected int getHeight(ItemStack stack){
            return 0; // Doesn't have any height of its own
        }

        @Override
        public int drawBackgroundLayer(int x, int y, ItemStack stack, float partialTicks, int mouseX, int mouseY){
            // It's more efficient to do GL state changes once in here
            GlStateManager.enableBlend();
            y = super.drawBackgroundLayer(x, y, stack, partialTicks, mouseX, mouseY);
            GlStateManager.disableBlend();
            return y;
        }

        @Override
        protected void drawBackground(int x, int y, ItemStack stack, float partialTicks, int mouseX, int mouseY){
            // Has no background of its own
        }

        @Override
        protected void drawForeground(int x, int y, ItemStack stack, int mouseX, int mouseY){
            //Has no text of its own
        }
    }

    private GuiWand.TooltipElement[] generateSpellEntries(int count){
        GuiWand.TooltipElement[] entries = new GuiWand.TooltipElement[count];
        for(int i=0; i<count; i++) entries[i] = new GuiWand.TooltipElementSpellEntry(i);
        return entries;
    }

    private class TooltipElementSpellEntry extends GuiWand.TooltipElementText {

        private final int index;

        public TooltipElementSpellEntry(int index){
            super(null, new Style().setColor(TextFormatting.BLUE), LINE_SPACING_NARROW);
            this.index = index;
        }

        private Spell getSpell(ItemStack stack){

            ItemStack spellBook = inventorySlots.getSlot(index).getStack();

            if(!spellBook.isEmpty() && spellBook.getItem() instanceof ItemSpellBook){
                return Spell.byMetadata(spellBook.getMetadata());
            }else{
                return ((ISpellCastingItem)stack.getItem()).getSpells(stack)[index];
            }
        }

        private boolean shouldFlash(ItemStack stack){
            ItemStack spellBook = inventorySlots.getSlot(index).getStack();
            return !spellBook.isEmpty() && spellBook.getItem() instanceof ItemSpellBook
                    && Spell.byMetadata(spellBook.getMetadata()) != ((ISpellCastingItem)stack.getItem()).getSpells(stack)[index];
        }

        private float getAlpha(float partialTicks){
            return (MathHelper.sin(0.2f * (mc.player.ticksExisted + partialTicks)) + 1) / 4 + 0.5f;
        }

        @Override
        protected boolean isVisible(ItemStack stack){
            return stack.getItem() instanceof ISpellCastingItem
                    && index < ((ISpellCastingItem)stack.getItem()).getSpells(stack).length;
        }

        @Override
        protected FontRenderer getFontRenderer(ItemStack stack){
            return Wizardry.proxy.shouldDisplayDiscovered(getSpell(stack), null) ? super.getFontRenderer(stack)
                    : mc.standardGalacticFontRenderer;
        }

        @Override
        protected int getColour(ItemStack stack){
            return shouldFlash(stack) ? DrawingUtils.makeTranslucent(0x000000, getAlpha(mc.getRenderPartialTicks()))
                    : super.getColour(stack);
        }

        @Override
        protected String getText(ItemStack stack){

            Spell spell = getSpell(stack);

            if(Wizardry.proxy.shouldDisplayDiscovered(spell, null)){
                return spell.getDisplayNameWithFormatting();
            }else{
                return SpellGlyphData.getGlyphName(spell, mc.world);
            }
        }

        @Override
        protected void drawBackground(int x, int y, ItemStack stack, float partialTicks, int mouseX, int mouseY){

            Spell spell = getSpell(stack);

            Minecraft.getMinecraft().renderEngine.bindTexture(Wizardry.proxy.shouldDisplayDiscovered(spell, null)
                    ? spell.getElement().getIcon() : Element.MAGIC.getIcon());

            if(shouldFlash(stack)){
                GlStateManager.color(1, 1, 1, getAlpha(partialTicks));
            }

            // Renders the little element icon
            DrawingUtils.drawTexturedRect(x, y, 8, 8);

            GlStateManager.color(1, 1, 1, 1);

        }

        @Override
        protected void drawForeground(int x, int y, ItemStack stack, int mouseX, int mouseY){
            super.drawForeground(x + 11, y, stack, mouseX, mouseY);
        }
    }

    private class TooltipElementUpgradeList extends GuiWand.TooltipElementText {

        public TooltipElementUpgradeList(int spaceAfter){
            super(I18n.format("container." + Wizardry.MODID + ":arcane_workbench.upgrades"),
                    new Style().setColor(TextFormatting.WHITE), spaceAfter, new GuiWand.TooltipElementUpgrades(0));
        }

        @Override
        protected int getHeight(ItemStack stack){
            return super.getHeight(stack) + LINE_SPACING_NARROW; // Gap between heading and upgrade icons
        }

        @Override
        protected boolean isVisible(ItemStack stack){
            return WandHelper.getTotalUpgrades(stack) > 0;
        }

    }

    private class TooltipElementUpgrades extends GuiWand.TooltipElement {

        private static final int ITEM_SIZE = 16;
        private static final int ITEM_SPACING = 2;

        public TooltipElementUpgrades(int spaceAfter){
            super(spaceAfter);
        }

        @Override
        protected boolean isVisible(ItemStack stack){
            return true; // Handled by parent
        }

        @Override
        protected int getHeight(ItemStack stack){
            int rows = 1 + (WandHelper.getTotalUpgrades(stack) * (ITEM_SIZE + ITEM_SPACING) - ITEM_SPACING)
                    / (TOOLTIP_WIDTH - TOOLTIP_BORDER * 2);
            return rows * (ITEM_SIZE + ITEM_SPACING) - ITEM_SPACING;
        }

        @Override
        protected void drawBackground(int x, int y, ItemStack stack, float partialTicks, int mouseX, int mouseY){

            GlStateManager.enableDepth();

            int x1 = 0;

            // Upgrades
            for(Item item : WandHelper.getSpecialUpgrades()){

                int level = WandHelper.getUpgradeLevel(stack, item);

                if(level > 0){

                    ItemStack upgrade = new ItemStack(item, level);

                    itemRender.renderItemAndEffectIntoGUI(upgrade, x + x1, y);
                    itemRender.renderItemOverlayIntoGUI(fontRenderer, upgrade, x + x1, y, null);

                    x1 += ITEM_SIZE + ITEM_SPACING;

                    if(x1 + ITEM_SIZE > TOOLTIP_WIDTH - TOOLTIP_BORDER * 2){
                        x1 = 0;
                        y += ITEM_SIZE + ITEM_SPACING;
                    }
                }
            }

            GlStateManager.disableDepth();
            GlStateManager.disableLighting(); // Whyyyyyy?
        }

        @Override
        protected void drawForeground(int x, int y, ItemStack stack, int mouseX, int mouseY){

            int x1 = 0;

            // Wand upgrade tooltips
            for(Item item : WandHelper.getSpecialUpgrades()){

                int level = WandHelper.getUpgradeLevel(stack, item);

                if(level > 0){
                    // The javadoc for isPointInRegion is ambiguous; what it means is that the REGION is
                    // relative to the GUI but the POINT isn't.
                    if(isPointInRegion(x + x1, y, ITEM_SIZE, ITEM_SIZE, mouseX, mouseY)){
                        ItemStack upgrade = new ItemStack(item, level);
                        renderToolTip(upgrade, mouseX - guiLeft, mouseY - guiTop);
                    }

                    x1 += ITEM_SIZE + ITEM_SPACING;

                    if(TOOLTIP_BORDER * 2 + x1 + ITEM_SIZE > TOOLTIP_WIDTH){
                        x1 = 0;
                        y += ITEM_SIZE + ITEM_SPACING;
                    }

                }
            }

        }

    }

    public static final ResourceLocation texture = new ResourceLocation(WizardryROTN.MODID,
            "textures/gui/container/arcane_workbench.png");

    private static final int TOOLTIP_WIDTH = 144;
    private static final int TOOLTIP_BORDER = 6;
    private static final int LINE_SPACING_WIDE = 5;
    private static final int LINE_SPACING_NARROW = 1;

    /** We report the actual size of the GUI to Minecraft when a wand is in so JEI doesn't overdraw it.
     * For calculations, we use the size without the tooltip, which is stored in this constant. */
    private static final int MAIN_GUI_WIDTH = 176;
    private static final int RUNE_LEFT = 38;
    private static final int RUNE_TOP = 22;
    private static final int RUNE_WIDTH = 100;
    private static final int RUNE_HEIGHT = 100;
    private static final int HALO_DIAMETER = 156;
    private static final int TEXTURE_WIDTH = 512;
    private static final int TEXTURE_HEIGHT = 512;
    private static final int ANIMATION_DURATION = 20;
    private final List<GuiWand.TooltipElement> tooltipElements = new ArrayList<>();
    public int animationTimer = 0;



}
