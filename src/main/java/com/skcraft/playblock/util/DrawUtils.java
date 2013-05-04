package com.skcraft.playblock.util;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

/**
 * Utility functions for drawing things in OpenGL.
 */
public final class DrawUtils {

    private DrawUtils() {
    }

    /**
     * Draw a rectangle.
     * 
     * @param x0 top left X
     * @param y0 top left Y
     * @param x1 bottom right X
     * @param y1 bottom right Y
     * @param color the color
     */
    public static void drawRect(float x0, float y0, float x1, float y1, int color) {
        float offset;

        if (x0 < x1) {
            offset = x0;
            x0 = x1;
            x1 = offset;
        }

        if (y0 < y1) {
            offset = y0;
            y0 = y1;
            y1 = offset;
        }

        float alpha = (color >> 24 & 255) / 255.0F;
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.instance;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(r, g, b, alpha);
        tessellator.startDrawingQuads();
        tessellator.addVertex(x0, y1, 0.0D);
        tessellator.addVertex(x1, y1, 0.0D);
        tessellator.addVertex(x1, y0, 0.0D);
        tessellator.addVertex(x0, y0, 0.0D);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

}
