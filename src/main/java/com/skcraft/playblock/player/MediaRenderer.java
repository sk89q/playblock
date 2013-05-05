package com.skcraft.playblock.player;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import uk.co.caprica.vlcj.binding.internal.libvlc_state_t;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallback;

import com.sun.jna.Memory;

/**
 * Renders a video from a player onto a surface.
 */
public final class MediaRenderer implements RenderCallback {

    private final int width;
    private final int height;
    private final int textureIndex;
    private DirectMediaPlayer player;
    private ByteBuffer buffer = null;
    private float bufferingPercent;

    /**
     * Create a new media renderer with the given width and height.
     * 
     * @param width the width
     * @param height the height
     */
    MediaRenderer(int width, int height) {
        this.width = width;
        this.height = height;
        this.textureIndex = createTexture(width, height);
    }

    /**
     * Set the VLCJ player used.
     * 
     * @param player the player
     */
    void setVLCJPlayer(DirectMediaPlayer player) {
        this.player = player;
    }

    /**
     * Get the underlying VLCJ player.
     * 
     * @return the player
     */
    DirectMediaPlayer getVLCJPlayer() {
        return player;
    }

    /**
     * Release resources and unload the player.
     */
    void release() {
        if (textureIndex >= 0) {
            try {
                GL11.glDeleteTextures(textureIndex);
            } catch (NullPointerException e) {
                // @TODO: Fix this NPE that is caused during total game shutdown
            }
        }
        
        player.stop();
        player.release();
        player = null;
    }

    /**
     * Create the internal texture used to draw the video.
     * 
     * @param width the width of the video
     * @param height the height of the video
     * @return the texture index
     */
    private int createTexture(int width, int height) {
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        GL11.glEnable(GL11.GL_TEXTURE_2D);

        int index = GL11.glGenTextures();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, index);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
                GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
                GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S,
                GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T,
                GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height,
                0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);

        return index;
    }

    /**
     * Play a media file or URL.
     * 
     * @param uri the path to play
     */
    public void playMedia(String uri) {
        player.playMedia(uri);
    }

    /**
     * Get the status of the current media.
     * 
     * @return the status
     */
    public MediaStatus getStatus() {
        libvlc_state_t state = player.getMediaState();
        if (state == null) {
            return MediaStatus.ERROR;
        }

        switch (state) {
        case libvlc_Buffering:
            return MediaStatus.BUFFERING;
        case libvlc_Ended:
            return MediaStatus.STOPPED;
        case libvlc_Error:
            return MediaStatus.ERROR;
        case libvlc_NothingSpecial:
            return MediaStatus.STOPPED;
        case libvlc_Opening:
            return MediaStatus.BUFFERING;
        case libvlc_Paused:
            return MediaStatus.PAUSED;
        case libvlc_Playing:
            return MediaStatus.PLAYING;
        case libvlc_Stopped:
            return MediaStatus.STOPPED;
        }

        return MediaStatus.STOPPED;
    }

    /**
     * Get the buffering status.
     * 
     * @return the buffering percentage
     */
    public float getBufferingPercent() {
        return bufferingPercent;
    }
    
    /**
     * Used because there doesn't seem to be a way to get the buffering progress.
     * 
     * @see PlayerEventListener the caller
     * @param bufferingPercent percent
     */
    void setBufferingPercent(float bufferingPercent) {
        this.bufferingPercent = bufferingPercent;
    }

    /**
     * Draw the display at the given location in 2D.
     * 
     * <p>The video will be resized accordingly.</p>
     * 
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param width the width of the screen
     * @param height the height of the screen
     */
    public void drawMedia(int x, int y, float width, float height) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureIndex);

        if (buffer != null) {
            GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, this.width,
                    this.height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        }

        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex3f(x, y, 0.0F);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex3f(x, y + height, 0.0F);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex3f(x + width, y, 0.0F);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex3f(x + width, y + height, 0.0F);
        GL11.glEnd();

        // DrawUtils.drawRect(x, y, x + width, y + height, 0xff000000);
    }

    @Override
    public void display(DirectMediaPlayer mediaPlayer, Memory[] nativeBuffers,
            BufferFormat bufferFormat) {
        buffer = nativeBuffers[0].getByteBuffer(0, width * height * 4);
    }

}
