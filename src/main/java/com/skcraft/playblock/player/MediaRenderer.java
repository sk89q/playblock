package com.skcraft.playblock.player;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;

import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallback;

import com.skcraft.playblock.util.DrawUtils;
import com.sun.jna.Memory;

/**
 * Renders a video from a player onto a surface.
 */
public final class MediaRenderer implements RenderCallback {

    private final MediaManager mediaManager;
    private final long creationTime;
    private final int width;
    private final int height;
    private final int textureIndex;
    private boolean released = false;
    private RendererState state = RendererState.INITIALIZING;
    private PlayerEventListener listener;
    private DirectMediaPlayer player;
    private ByteBuffer buffer = null;

    /**
     * Create a new media renderer with the given width and height.
     * 
     * @param width
     *            the width
     * @param height
     *            the height
     * @param textureIndex
     *            the text of the index
     */
    MediaRenderer(MediaManager mediaManager, int width, int height, int textureIndex) {
        this.mediaManager = mediaManager;
        this.creationTime = System.currentTimeMillis();
        this.width = width;
        this.height = height;
        this.textureIndex = textureIndex;
    }

    /**
     * Initialize the instance and create a new player.
     * 
     * @param factory
     *            the factory for creating a new player
     * @param volume
     *            the initial volume
     */
    void initialize(final MediaPlayerFactory factory, final float volume) {
        final MediaRenderer instance = this;

        // Release the VLC player instance in the dedicated thread
        mediaManager.executeThreadSafe(new Runnable() {
            @Override
            public void run() {
                // Create the VLC media player instance
                DirectMediaPlayer player = factory.newDirectMediaPlayer("RGBA", width, height, width * 4, instance);
                player.setPlaySubItems(true);
                player.setRepeat(true);
                player.addMediaPlayerEventListener(listener = new PlayerEventListener(instance));
                player.setVolume((int) (volume * 100));

                instance.player = player;
            }
        });
    }

    /**
     * Stop and release the player, then lose references.
     */
    void release() {
        final MediaRenderer instance = this;

        // Release the VLC player instance in the dedicated thread
        mediaManager.executeThreadSafe(new Runnable() {
            @Override
            public void run() {
                player.setVolume(0); // The calls below may block for a bit
                player.stop();
                player.release();

                player = null; // Lose reference
            }
        });
    }

    /**
     * Get the time that this renderer was created at.
     * 
     * @return UNIX timestamp in milliseconds
     */
    public long getCreationTime() {
        return creationTime;
    }

    /**
     * Return true if this renderer is being released or it has been released.
     * 
     * @return true if the renderer has been released
     */
    public boolean isReleased() {
        return released;
    }

    /**
     * Mark this instance for release so that any object using this renderer
     * (and also {@link MediaManager}) knows that it has been released already.
     * 
     * @see MediaManager#release(MediaRenderer) the method where things are
     *      actually unloaded
     */
    void markForRelease() {
        released = true;
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
     * Get the texture index for this instance.
     * 
     * @return the texture index
     */
    int getTextureIndex() {
        return textureIndex;
    }

    /**
     * Get the width of the screen.
     * 
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the height of the screen.
     * 
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Play a media file or URL.
     * 
     * @param uri
     *            the path to play
     * @param position
     *            milliseconds to skip in the video
     * @param repeat
     *            true to repeat the video
     */
    public void playMedia(final String uri, final long position, final boolean repeat) {
        if (isReleased()) {
            throw new RuntimeException("Cannot play media on released player");
        }

        final long startTime = System.currentTimeMillis() - position;

        mediaManager.executeThreadSafe(new Runnable() {
            @Override
            public void run() {
                listener.setSeekPosition(position);
                player.playMedia(uri); // player should NOT be null
                player.setRepeat(repeat);
                buffer = null;
            }
        });
    }

    /**
     * Play a media file or URL, defaulting to repeat.
     * 
     * @param uri
     *            the path to play
     */
    public void playMedia(final String uri) {
        playMedia(uri, -1, true);
    }

    /**
     * Stop whatever is playing.
     */
    public void stop() {

        mediaManager.executeThreadSafe(new Runnable() {
            @Override
            public void run() {
                player.stop();
            }
        });
    }

    /**
     * Set the volume of the player.
     * 
     * @param volume
     */
    public void setVolume(float volume) {
        if (isReleased()) {
            return;
        }

        player.setVolume((int) (volume * 100));
    }

    /**
     * Get the state of this renderer (and the currently playing media)
     * 
     * @return the state
     */
    public RendererState getState() {
        if (isReleased()) {
            return RendererState.RELEASED;
        }

        return state;
    }

    /**
     * Set the state of this renderer.
     * 
     * <p>
     * This is used by only {@link PlayerEventListener} so that state
     * information can be updated on this renderer. Do not call this method from
     * anywhere else.
     * </p>
     * 
     * @see PlayerEventListener calls this method
     * @param state
     *            the new state
     */
    void setState(RendererState state) {
        this.state = state;
    }

    /**
     * Draw the display at the given location in 2D.
     * 
     * <p>
     * The video will be resized accordingly.
     * </p>
     * 
     * @param x
     *            the X coordinate
     * @param y
     *            the Y coordinate
     * @param width
     *            the width of the screen
     * @param height
     *            the height of the screen
     */
    public void drawMedia(int x, int y, float width, float height) {
        if (buffer != null && !isReleased()) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureIndex);

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

            GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, this.width, this.height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        } else {
            // Because we don't zero out the texture data for the texture that
            // we
            // had created for this player, it may contain existing texture data
            // that may have been used for who knows what before (UI drawing,
            // another game, another video), and it's best we not draw that!
            DrawUtils.drawRect(x, y, x + width, y + height, 0xff000000);
        }
    }

    @Override
    public void display(DirectMediaPlayer mediaPlayer, Memory[] nativeBuffers, BufferFormat bufferFormat) {
        if (released) {
            return;
        }

        buffer = nativeBuffers[0].getByteBuffer(0, width * height * 4);
    }

}
