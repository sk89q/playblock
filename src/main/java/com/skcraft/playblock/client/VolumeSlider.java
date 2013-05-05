package com.skcraft.playblock.client;

import com.skcraft.playblock.LKey;
import com.skcraft.playblock.PlayBlock;
import com.skcraft.playblock.player.MediaManager;
import com.skcraft.playblock.util.GuiSlider;

public class VolumeSlider extends GuiSlider {

    private final MediaManager mediaManager;

    public VolumeSlider(int id, int x, int y, int width, int height,
            MediaManager mediaManager) {
        super(id, x, y, width, height);
        this.mediaManager = mediaManager;
        update();
    }

    @Override
    public String getValueText(float value) {
        return LKey.VOLUME + ": " + Math.round(value * 200) + "%";
    }

    @Override
    public float getInitialValue() {
        return mediaManager.getVolume() / 2;
    }

    @Override
    public void onValue(float value) {
        mediaManager.setVolume(value * 2);
    }

}
