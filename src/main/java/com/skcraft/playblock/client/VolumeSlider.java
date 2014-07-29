package com.skcraft.playblock.client;

import com.skcraft.playblock.player.MediaManager;
import com.skcraft.playblock.util.GuiSlider;
import com.skcraft.playblock.util.StringUtils;

public class VolumeSlider extends GuiSlider {

    private final MediaManager mediaManager;

    public VolumeSlider(int id, int x, int y, int width, int height, MediaManager mediaManager) {
        super(id, x, y, width, height);
        this.mediaManager = mediaManager;
        update();
    }

    @Override
    public String getValueText(float value) {
        return StringUtils.translate("options.volume") + ": " + Math.round(value * 200) + "%";
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
