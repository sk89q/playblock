package com.skcraft.playblock.projector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

@SideOnly(Side.CLIENT)
public class ProjectorQueueSlot extends Gui {
    
    public static final int WIDTH = 134;
    public static final int HEIGHT = 13;
    private int xPos, yPos;
    private boolean selected;
    private Minecraft mc;
    private String name;
    private ProjectorQueueGui parentScreen;
    
    public ProjectorQueueSlot(ProjectorQueueGui parent, Minecraft mc, String name) {
        this.parentScreen = parent;
        this.mc = mc;
        this.name = name;
    }
    
    /**
     * Draws the slot.
     * 
     * @param position position in the list
     * @param guiLeft
     * @param guiTop
     */
    public void drawSlot(int position, int guiLeft, int guiTop) {
        xPos = guiLeft + 57;
        yPos = guiTop + 55 + position * HEIGHT;
        if(selected) {
            drawRect(xPos, yPos, xPos + WIDTH, yPos + HEIGHT, -16777216);
        }
        
        mc.fontRenderer.drawString(name, xPos + 3, yPos +
                (HEIGHT - mc.fontRenderer.FONT_HEIGHT) / 2, 14737632);
    }

    public void mouseClicked(int x, int y, int buttonClicked) {
        int right = xPos + WIDTH;
        int bottom = yPos + HEIGHT;
        if(x >= xPos && x < right && y >= yPos && y < bottom) {
            parentScreen.setSelectedSlot(this);
        }
    }
    
    /**
     * Sets this as the selected slot
     * 
     * @param selected
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
