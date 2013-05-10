package com.skcraft.playblock.player;

import java.util.logging.Level;

import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

import com.skcraft.playblock.PlayBlock;

/**
 * Logs semi-useful debugging messages to console.
 */
public class PlayerEventListener extends MediaPlayerEventAdapter {
    
    private final MediaRenderer renderer;
    
    public PlayerEventListener(MediaRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void error(MediaPlayer mediaPlayer) {
        PlayBlock.log(Level.INFO, "Media has encountered an error!");
    }

    @Override
    public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t media, String mrl) {
        PlayBlock.logf(Level.INFO, "Now playing %s...", mrl);
    }

}
