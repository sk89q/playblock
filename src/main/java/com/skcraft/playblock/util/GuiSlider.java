package com.skcraft.playblock.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiSlider extends GuiButton {

    private float value = 1;
    private boolean dragging = false;

    public GuiSlider(int id, int x, int y, int width, int height) {
        super(id, x, y, width, 20, "");
    }

    protected void update() {
        this.value = getInitialValue();
        this.displayString = getValueText(value);
    }

    /**
     * Gets the string displayed on the slider.
     * 
     * @param value
     *            the value
     * @return the text
     */
    public abstract String getValueText(float value);

    /**
     * Returns the initial value.
     * 
     * @return the value
     */
    public abstract float getInitialValue();

    /**
     * Gets called when the value changes.
     * 
     * @param value
     *            the value
     */
    public abstract void onValue(float value);

    /**
     * Gets the current value.
     * 
     * @return the value
     */
    public float getValue() {
        return value;
    }

    @Override
    public int getHoverState(boolean par1) {
        return 0;
    }

    @Override
    protected void mouseDragged(Minecraft minecraft, int x, int y) {
        if (this.visible) {
            if (this.dragging) {
                this.value = (float) (x - (this.xPosition + 4)) / (float) (this.width - 8);

                if (this.value < 0) {
                    this.value = 0;
                }

                if (this.value > 1) {
                    this.value = 1;
                }

                onValue(value);
                this.displayString = getValueText(value);
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            this.drawTexturedModalRect(this.xPosition + (int) (this.value * (this.width - 8)), this.yPosition, 0, 66, 4, 20);
            this.drawTexturedModalRect(this.xPosition + (int) (this.value * (this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
        }
    }

    @Override
    public boolean mousePressed(Minecraft minecraft, int x, int y) {
        if (super.mousePressed(minecraft, x, y)) {
            this.value = (float) (x - (this.xPosition + 4)) / (float) (this.width - 8);

            if (this.value < 0) {
                this.value = 0;
            }

            if (this.value > 1) {
                this.value = 1;
            }

            onValue(value);
            this.displayString = getValueText(value);
            this.dragging = true;

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void mouseReleased(int par1, int par2) {
        this.dragging = false;
    }

}
