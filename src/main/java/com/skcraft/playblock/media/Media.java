package com.skcraft.playblock.media;

import com.skcraft.playblock.util.Validate;

/**
 * Stores available meta-data about a piece of media.
 */
public class Media {

    private String uri;
    private String title;
    private String description;
    private String thumbnail;
    private String creator;
    private Long length;

    public Media(String uri) {
        setUri(uri);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        Validate.notNull(uri);
        this.uri = uri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        if (length != null && length <= 0) {
            length = null;
        }
        this.length = length;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String uri) {
        thumbnail = uri;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

}
