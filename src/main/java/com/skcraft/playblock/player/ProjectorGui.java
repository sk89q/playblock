package com.skcraft.playblock.player;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * The GUI for the projector.
 */
@SideOnly(Side.CLIENT)
public class ProjectorGui extends GuiScreen {

    public static final int ID = 0;
    private static final int xSize = 247;
    private static final int ySize = 165;
    private ProjectorTileEntity tile;
    private GuiTextField uriField, heightField, widthField, triggerRangeField,
            fadeRangeField;
    private GuiButton buttonSet;
    
    private float projectorWidth, projectorHeight, triggerRange, fadeRange;
    private String uri;

    public ProjectorGui(ProjectorTileEntity tileEntity) {
        tile = tileEntity;
        uri = tileEntity.getUri();
        projectorWidth = tileEntity.getWidth();
        projectorHeight = tileEntity.getHeight();
        triggerRange = tileEntity.getTriggerRange();
        fadeRange = tileEntity.getFadeRange();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @Override
    public void initGui() {
        this.controlList.clear();
        Keyboard.enableRepeatEvents(true);
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;

        this.controlList.add(buttonSet = 
                new GuiButton(0, left + 160, top + 125, 80, 20, "Update"));

        uriField = new GuiTextField(this.fontRenderer, left + 60, top + 17,
                170, this.fontRenderer.FONT_HEIGHT + 5);
        initTextField(uriField, 100, uri);

        heightField = new GuiTextField(this.fontRenderer, left + 130, top + 37,
                50, this.fontRenderer.FONT_HEIGHT + 5);
        initTextField(heightField, 10, Float.toString(projectorHeight));

        widthField = new GuiTextField(this.fontRenderer, left + 60, top + 37,
                50, this.fontRenderer.FONT_HEIGHT + 5);
        initTextField(widthField, 10, Float.toString(projectorWidth));

        triggerRangeField = new GuiTextField(this.fontRenderer, left + 60,
                top + 57, 50, this.fontRenderer.FONT_HEIGHT + 5);
        initTextField(triggerRangeField, 10, Float.toString(triggerRange));

        fadeRangeField = new GuiTextField(this.fontRenderer, left + 60,
                top + 77, 50, this.fontRenderer.FONT_HEIGHT + 5);
        initTextField(fadeRangeField, 10, Float.toString(fadeRange));
    }

    /**
     * Prepare a text field for entry.
     * 
     * @param field the field
     * @param length the maximum length of the string
     * @param text the initial text
     */
    private void initTextField(GuiTextField field, int length, String text) {
        field.setVisible(true);
        field.setMaxStringLength(length);
        field.setEnableBackgroundDrawing(true);
        field.setCanLoseFocus(true);
        field.setFocused(false);
        field.setText(text);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            // Make a new tile entity so we don't change ours quite yet
            // The server may reject our changes
            ProjectorTileEntity newTile = new ProjectorTileEntity(tile);
            newTile.setUri(uri);
            newTile.setHeight(projectorHeight);
            newTile.setWidth(projectorWidth);
            newTile.setTriggerRange(triggerRange);
            newTile.setFadeRange(fadeRange);

            // Now tell the server about the changes
            PacketDispatcher.sendPacketToServer(newTile.getUpdatePacket());

            this.mc.displayGuiScreen((GuiScreen) null);
            this.mc.setIngameFocus();
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float par3) {
        int texture = mc.renderEngine.getTexture("/playblock/gui/projector_bg.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture(texture);
        int left = (width - xSize) / 2;
        int top = (height - ySize) / 2;
        drawTexturedModalRect(left, top, 0, 0, xSize, ySize);
        uriField.drawTextBox();
        heightField.drawTextBox();
        widthField.drawTextBox();
        triggerRangeField.drawTextBox();
        fadeRangeField.drawTextBox();

        fontRenderer.drawString("URL:", left + 10, top + 20, 0xff999999);
        fontRenderer.drawString("Size:", left + 10, top + 40, 0xff999999);
        fontRenderer.drawString("x", left + 117, top + 40, 0xff999999);
        fontRenderer.drawString("Turn on:", left + 10, top + 60, 0xff999999);
        fontRenderer.drawString("block(s) away", left + 117, top + 60, 0xff999999);
        fontRenderer.drawString("Turn off:", left + 10, top + 80, 0xff999999);
        fontRenderer.drawString("block(s) away", left + 117, top + 80, 0xff999999);
        fontRenderer.drawString("PlayBlock Projector", left + 10, top + 132, 0xffffffff);

        super.drawScreen(mouseX, mouseY, par3);
    }

    @Override
    protected void mouseClicked(int x, int y, int buttonClicked) {
        super.mouseClicked(x, y, buttonClicked);
        uriField.mouseClicked(x, y, buttonClicked);
        heightField.mouseClicked(x, y, buttonClicked);
        widthField.mouseClicked(x, y, buttonClicked);
        triggerRangeField.mouseClicked(x, y, buttonClicked);
        fadeRangeField.mouseClicked(x, y, buttonClicked);
    }

    @Override
    protected void keyTyped(char key, int par2) {
        super.keyTyped(key, par2);
        if (uriField.isFocused()) {
            uriField.textboxKeyTyped(key, par2);
            uri = uriField.getText();
        }
        if (Character.isDigit(key) || par2 == 14 || par2 == 52 || par2 == 199
                || par2 == 203 || par2 == 205 || par2 == 207 || par2 == 211) {
            if (heightField.isFocused()) {
                heightField.textboxKeyTyped(key, par2);
                if (heightField.getText().length() != 0) {
                    projectorHeight = Float.parseFloat(heightField.getText());
                }
            } else if (widthField.isFocused()) {
                widthField.textboxKeyTyped(key, par2);
                if (widthField.getText().length() != 0) {
                    projectorWidth = Float.parseFloat(widthField.getText());
                }
            } else if (triggerRangeField.isFocused()) {
                triggerRangeField.textboxKeyTyped(key, par2);
                if (triggerRangeField.getText().length() != 0) {
                    triggerRange = Float
                            .parseFloat(triggerRangeField.getText());
                }
            } else if (fadeRangeField.isFocused()) {
                fadeRangeField.textboxKeyTyped(key, par2);
                if (fadeRangeField.getText().length() != 0) {
                    fadeRange = Float
                            .parseFloat(fadeRangeField.getText());
                }
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
