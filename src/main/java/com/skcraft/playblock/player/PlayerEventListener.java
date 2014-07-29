package com.skcraft.playblock.player;

import java.util.List;

import org.apache.logging.log4j.Level;

import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.binding.internal.libvlc_state_t;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

import com.skcraft.playblock.PlayBlock;

/**
 * Logs semi-useful debugging messages to console.
 */
public class PlayerEventListener extends MediaPlayerEventAdapter {

    private final MediaRenderer renderer;
    private long seekPosition = 0;

    public PlayerEventListener(MediaRenderer renderer) {
        this.renderer = renderer;
    }

    public long getSeekPosition() {
        return seekPosition;
    }

    public void setSeekPosition(long seekPosition) {
        this.seekPosition = seekPosition;
    }

    @Override
    public void buffering(MediaPlayer mediaPlayer, float newCache) {
        // This would override a PLAYING state
        renderer.setState(getPrimaryState());
    }

    @Override
    public void finished(MediaPlayer mediaPlayer) {
        // Used for YouTube videos
        List<String> subItems = mediaPlayer.subItems();
        if (subItems != null && !subItems.isEmpty()) {
            String subItemURI = subItems.get(0);
            mediaPlayer.playMedia(subItemURI);
        } else {
            renderer.setState(RendererState.STOPPED);
        }
    }

    @Override
    public void opening(MediaPlayer mediaPlayer) {
        renderer.setState(getPrimaryState());
    }

    @Override
    public void paused(MediaPlayer mediaPlayer) {
        renderer.setState(RendererState.PAUSED);
    }

    @Override
    public void playing(MediaPlayer mediaPlayer) {
        renderer.setState(RendererState.PLAYING);
        long position = getSeekPosition();
        if (position > 0) {
            mediaPlayer.setTime(position);
        }
    }

    @Override
    public void stopped(MediaPlayer mediaPlayer) {
        renderer.setState(RendererState.STOPPED);
    }

    @Override
    public void error(MediaPlayer mediaPlayer) {
        renderer.setState(RendererState.ERROR);
        PlayBlock.log(Level.INFO, "Media has encountered an error!");
    }

    @Override
    public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t media, String mrl) {
        PlayBlock.logf(Level.INFO, "Now playing %s...", mrl);
    }

    private RendererState getPrimaryState() {
        libvlc_state_t state = renderer.getVLCJPlayer().getMediaState();

        if (state == null) {
            return RendererState.ERROR;
        }

        switch (state) {
        case libvlc_Buffering:
            return RendererState.BUFFERING;
        case libvlc_Ended:
            return RendererState.STOPPED;
        case libvlc_Error:
            return RendererState.ERROR;
        case libvlc_NothingSpecial:
            return RendererState.STOPPED;
        case libvlc_Opening:
            return RendererState.BUFFERING;
        case libvlc_Paused:
            return RendererState.PAUSED;
        case libvlc_Playing:
            return RendererState.PLAYING;
        case libvlc_Stopped:
            return RendererState.STOPPED;
        }

        return RendererState.STOPPED;
    }

}
