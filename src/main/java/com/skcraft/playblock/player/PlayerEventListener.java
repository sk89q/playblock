package com.skcraft.playblock.player;

import java.util.logging.Level;

import com.skcraft.playblock.PlayBlock;

import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import cpw.mods.fml.common.FMLLog;

/**
 * Logs semi-useful debugging messages to console.
 */
public class PlayerEventListener extends MediaPlayerEventAdapter {
    
    private final MediaRenderer renderer;
    
    public PlayerEventListener(MediaRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void buffering(MediaPlayer mediaPlayer, float percent) {
        renderer.setBufferingPercent(percent);
    }

    @Override
    public void error(MediaPlayer mediaPlayer) {
        PlayBlock.log(Level.INFO, "Media has encountered an error!");
    }

    @Override
    public void finished(MediaPlayer mediaPlayer) {
        PlayBlock.log(Level.INFO, "Media has finished");
    }

    @Override
    public void opening(MediaPlayer mediaPlayer) {
        PlayBlock.log(Level.INFO, "Media is opening...");
    }

    @Override
    public void paused(MediaPlayer mediaPlayer) {
        PlayBlock.log(Level.INFO, "Media has paused");
    }

    @Override
    public void playing(MediaPlayer mediaPlayer) {
        PlayBlock.log(Level.INFO, "Media is playing");
    }

    @Override
    public void stopped(MediaPlayer mediaPlayer) {
        PlayBlock.log(Level.INFO, "Media stopped");
    }

    @Override
    public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t media, String mrl) {
        PlayBlock.logf(Level.INFO, "Now playing %s...", mrl);
    }

}
