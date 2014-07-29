package com.skcraft.playblock.player;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * A simple texture cache to re-use generated textures that is optimized to have
 * only one texture created at a time.
 */
public class TextureCache {

    private int lastWidth;
    private int lastHeight;
    private int lastIndex;
    private boolean lastFree = false;

    /**
     * Create the texture used to draw the video.
     * 
     * @param width
     *            the width of the video
     * @param height
     *            the height of the video
     * @return the texture index
     */
    public int createTexture(int width, int height) {
        if (lastIndex > 0 && lastFree) {
            if (lastWidth == width && lastHeight == height) {
                lastFree = false;
                return lastIndex;
            } else {
                // Well, forget this cached texture then!
                tryDeleteTexture(lastIndex);
                lastIndex = 0;
            }
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);

        int index = GL11.glGenTextures();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, index);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);

        lastIndex = index;
        lastWidth = width;
        lastHeight = height;
        lastFree = false;

        return index;
    }

    /**
     * Delete a texture.
     * 
     * @param index
     *            the texture index
     */
    public void deleteTexture(int index) {
        if (index == lastIndex) {
            lastFree = true;
        } else {
            tryDeleteTexture(index);
        }
    }

    /**
     * Try to delete a texture.
     * 
     * @param index
     *            the texture index
     */
    private void tryDeleteTexture(int index) {
        try {
            GL11.glDeleteTextures(index);
        } catch (NullPointerException e) {
            // This occurs when everything is being shutdown, and this is
            // a hacky fix to prevent a total crash
        }
    }

}
