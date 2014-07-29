package com.skcraft.playblock.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiScrollbar extends Gui {

    private int xPos, yPos, scrollHeight, textureX, textureY, width, height;
    private String texturePath;
    private float currentScroll = 0;
    private boolean isScrolling = false;
    private boolean wasClicking = false;
    private boolean enabled = false;
    private Minecraft mc;

    public GuiScrollbar(Minecraft mc, int xPos, int yPos, int scrollHeight, int textureX, int textureY, int textureWidth, int textureHeight, String texturePath) {
        this.mc = mc;
        this.xPos = xPos;
        this.yPos = yPos;
        this.scrollHeight = scrollHeight;
        this.textureX = textureX;
        this.textureY = textureY;
        this.width = textureWidth;
        this.height = textureHeight;
        this.texturePath = texturePath;
    }

    public void drawScrollbar(int mouseX, int mouseY) {
        boolean mouseDown = Mouse.isButtonDown(0);
        int scrollRight = xPos + width;
        int scrollBottom = yPos + scrollHeight;
        if (!wasClicking && mouseDown && mouseX >= xPos && mouseX < scrollRight && mouseY >= yPos && mouseY < scrollBottom) {
            isScrolling = enabled;
        }

        if (!mouseDown) {
            isScrolling = false;
        }

        wasClicking = mouseDown;

        if (isScrolling) {
            currentScroll = (mouseY - yPos - (height / 2.0f)) / ((float) (scrollBottom - yPos) - height);

            if (currentScroll < 0) {
                currentScroll = 0;
            } else if (currentScroll > 1) {
                currentScroll = 1;
            }
        }

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.renderEngine.bindTexture(new ResourceLocation(texturePath));
        drawTexturedModalRect(xPos, yPos + (int) ((scrollBottom - yPos - height) * currentScroll), textureX, textureY, width, height);
    }

    public void handleMouseInput(int totalItems, int seenItems) {
        int wheelDelta = Mouse.getEventDWheel();

        if (wheelDelta != 0 && enabled) {
            int unseenSlots = totalItems - seenItems;

            if (wheelDelta < 0)
                wheelDelta = -1;
            else if (wheelDelta > 0)
                wheelDelta = 1;

            currentScroll -= (float) wheelDelta / (float) unseenSlots;

            if (currentScroll < 0) {
                currentScroll = 0;
            } else if (currentScroll > 1) {
                currentScroll = 1;
            }
        }
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public float getCurrentScroll() {
        return currentScroll;
    }

    public void setCurrentScroll(float scroll) {
        currentScroll = scroll;
    }

}
