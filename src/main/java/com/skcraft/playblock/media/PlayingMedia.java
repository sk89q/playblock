package com.skcraft.playblock.media;

import com.skcraft.playblock.util.Validate;

/**
 * Holds information about media that is currently playing.
 */
public class PlayingMedia {

    private final Media media;
    private final long startTime;

    private PlayingMedia(Media media, long startTime) {
        Validate.notNull(media);

        this.media = media;
        this.startTime = startTime;
    }

    public Media getMedia() {
        return media;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getPosition() {
        if (startTime < 0) {
            return -1;
        }

        return System.currentTimeMillis() - startTime;
    }

    public long getCalculatedPosition() {
        if (startTime < 0) {
            return 0;
        }

        return System.currentTimeMillis() - startTime;
    }

    public static PlayingMedia fromRelative(Media media, long position) {
        if (position < 0) {
            return new PlayingMedia(media, -1);
        }
        return new PlayingMedia(media, System.currentTimeMillis() - position);
    }

    public static PlayingMedia fromAbsolute(Media media, long startTime) {
        if (startTime < 0) {
            return new PlayingMedia(media, -1);
        }
        return new PlayingMedia(media, startTime);
    }

    public static PlayingMedia fromNow(Media media) {
        return new PlayingMedia(media, System.currentTimeMillis());
    }

}
