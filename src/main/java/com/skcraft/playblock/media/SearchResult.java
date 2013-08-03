package com.skcraft.playblock.media;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class SearchResult extends Gui {

    private static final int WIDTH = 175;
    private static final int HEIGHT = 38;
    private boolean selected = false;
    private int xPos, yPos;
    private Minecraft mc;
    private Media media;
    private SearchGui parentScreen;
    private List<String> formattedTitle;

    public SearchResult(SearchGui parent, Media media, Minecraft mc) {
        this.parentScreen = parent;
        this.media = media;
        this.mc = mc;
        this.formattedTitle = mc.fontRenderer.listFormattedStringToWidth(media.getTitle(), WIDTH - 2);
    }

    public void drawResult(int position, int guiLeft, int guiTop) {
        xPos = guiLeft + 37;
        yPos = guiTop + 44 + position * HEIGHT;
        
        if(selected) {
            drawRect(xPos, yPos, xPos + WIDTH, yPos + HEIGHT, -16777216);
        }
        
        // TODO Display the thumbnail
        
        mc.fontRenderer.drawString(formattedTitle.get(0), xPos + 2, yPos + 
                (HEIGHT - mc.fontRenderer.FONT_HEIGHT) / 2 - 11, 14737632);
        
        if(formattedTitle.size() > 1) {
            String line = formattedTitle.get(1);
            if(mc.fontRenderer.getStringWidth(line) > WIDTH - 6 || formattedTitle.size() > 2) {
                line = mc.fontRenderer.trimStringToWidth(line, WIDTH - 6).concat("...");
            }
            mc.fontRenderer.drawString(line, xPos + 2, yPos + 
                    (HEIGHT - mc.fontRenderer.FONT_HEIGHT) / 2 - 1, 14737632);
        }
        
        mc.fontRenderer.drawString("by " + media.getCreator(), xPos + 2, yPos + 
                (HEIGHT - mc.fontRenderer.FONT_HEIGHT) / 2 + (formattedTitle.size() > 1 ? 8 : -2), 0xff6E6E6E);
    }

    public void mouseClicked(int x, int y, int buttonClicked) {
        int right = xPos + WIDTH;
        int bottom = yPos + HEIGHT;
        if(x >= xPos && x < right && y >= yPos && y < bottom) {
            parentScreen.setSelectedResult(this);
        }
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
