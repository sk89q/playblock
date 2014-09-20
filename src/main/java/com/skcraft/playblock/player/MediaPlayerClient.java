package com.skcraft.playblock.player;

import net.minecraft.nbt.NBTTagCompound;

import com.skcraft.playblock.PlayBlock;
import com.skcraft.playblock.media.Media;
import com.skcraft.playblock.media.MediaResolver;
import com.skcraft.playblock.media.PlayingMedia;

/**
 * This class manages the client side of the media player, notably the
 * activation and actual video playing. Rendering is off-loaded to
 * {@link MediaRenderer}.
 */
public class MediaPlayerClient extends MediaPlayer {

    private static final int MAX_VIDEO_SIZE = 850;
    private static final int RESOLUTION_FACTOR = 64;

    private final MediaManager mediaManager;

    private PlayingMedia playing;
    private MediaRenderer renderer;

    /**
     * Construct a new instance.
     */
    public MediaPlayerClient() {
        mediaManager = PlayBlock.getClientRuntime().getMediaManager();
    }

    /**
     * Set the currently playing media clip on this client. This may refer to
     * the URI property (from {@link #getUri()}), but it could also be from
     * something else.
     * 
     * <p>
     * Some validation is performed on the given URI &mdash; if the given URI
     * isn't considered valid according to
     * {@link MediaResolver#canPlayUri(String)}, then the currently playing
     * media clip will be set to null.
     * </p>
     * 
     * @param uri
     *            the URI, null to play nothing
     * @param position
     *            a non-negative position to start from, in milliseconds,
     *            otherwise -1 to indicate that the video should not be time
     *            shifted
     */
    private void setPlaying(String uri, long position) {
        if (uri != null && MediaResolver.canPlayUri(uri)) {
            playing = PlayingMedia.fromRelative(new Media(uri), position);
        } else {
            playing = null;
        }
    }

    /**
     * Return whether there is actually something to play.
     * 
     * <p>
     * There could be a renderer assigned but nothing to actually playing!
     * </p>
     * 
     * @return true if there's something to play
     */
    public boolean hasSomethingToPlay() {
        return playing != null;
    }

    /**
     * Get the renderer assigned to this tile entity.
     * 
     * @return the renderer, or possibly null if there's no renderer yet
     */
    public MediaRenderer getRenderer() {
        return renderer;
    }

    /**
     * Acquire a renderer and start playing the video if possible.
     */
    private void setupRenderer() {
        int videoWidth = (int) Math.min(MAX_VIDEO_SIZE, getWidth() * RESOLUTION_FACTOR);
        int videoHeight = (int) Math.min(MAX_VIDEO_SIZE, getHeight() * RESOLUTION_FACTOR);
        renderer = mediaManager.acquireRenderer(videoWidth, videoHeight);
    }

    /**
     * Tries to play the media on this projector.
     * 
     * <p>
     * A renderer will be acquired, or a new one will be setup if the width and
     * height have changed.
     * </p>
     */
    private void playNewMedia() {
        if (renderer == null) { // Create a new renderer if we don't have one
            setupRenderer();
            if (renderer == null) { // Stop if there is still no renderer
                return;
            }
        } else if (renderer.getWidth() != getWidth() || renderer.getHeight() != getHeight()) {
            // Width or height change? Re-make the renderer
            release();
            setupRenderer();
        }

        if (playing != null) {
            String uri = playing.getMedia().getUri();
            renderer.playMedia(uri, playing.getCalculatedPosition(), !inQueueMode());
        } else {
            renderer.stop();
        }
    }

    /**
     * Start playing (if possible).
     * 
     * <p>
     * This can be called repeatedly even if something is already playing.
     * </p>
     */
    public void enable() {
        if (renderer == null) { // We have nothing active
            if (mediaManager.hasNoRenderer()) { // Is there a free renderer?
                playNewMedia(); // Yay!
            }
        }
    }

    /**
     * Stop playing.
     * 
     * <p>
     * This can be called repeatedly even if nothing is playing.
     * </p>
     */
    public void disable() {
        release();
    }

    /**
     * Detach the renderer from this instance and also stop the video.
     */
    public void release() {
        if (renderer != null) {
            mediaManager.release(renderer);
            renderer = null;
        }
    }

    private void setPlayingFromTag(NBTTagCompound tag) {
        if (inQueueMode()) {
            String playedUri = tag.getString("playedUri");

            if (!playedUri.isEmpty()) {
                setPlaying(playedUri, tag.getInteger("position"));
            } else {
                setPlaying(null, -1);
            }
        } else if (tag.hasKey("uri")) {
            setPlaying(getUri(), -1);
        }

        if (renderer != null) {
            playNewMedia(); // Switch streams
        }
    }

    @Override
    public void writeNetworkedNBT(NBTTagCompound tag) {
        // State NBT can only come from the server
    }

    @Override
    public void readNetworkedNBT(NBTTagCompound tag) {
        if (tag.hasKey("uri")) {
            fromSharedNbt(tag);
        }

        if (tag.hasKey("playedUri") || tag.hasKey("uri")) {
            setPlayingFromTag(tag);
        }
    }

}
