package com.skcraft.playblock;

import cpw.mods.fml.common.registry.LanguageRegistry;

/**
 * Stores localization keys.
 */
public enum LKey {
    
    PLAYBLOCK_OPTIONS("playblock.options.name", "PlayBlock Options"),
    URL("playblock.options.url", "URL:"),
    SCREEN_SIZE("playblock.options.screenSize", "Size:"),
    TURN_ON("playblock.options.turnOn", "Turn On:"),
    TURN_OFF("playblock.options.turnOff", "Turn Off:"),
    BLOCKS_AWAY("playblock.options.blocksAway", "blocks away"),
    WEBSITE("playblock.options.website", "Website..."),
    VOLUME("playblock.options.volume", "Volume"),
    DONE("playblock.gui.done", "Done");
    
    private final String key;
    private final String enUsDefault;

    /**
     * Store a new string.
     * 
     * @param key the key
     */
    private LKey(String key) {
        this.key = key;
        this.enUsDefault = null;
    }

    /**
     * Store a new string, with an en-US default.
     * 
     * @param key the key
     * @param enUsDefault the default en-US translation
     */
    private LKey(String key, String enUsDefault) {
        this.key = key;
        this.enUsDefault = enUsDefault;
        
        LanguageRegistry.instance().addStringLocalization(key, enUsDefault);
    }
    
    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        String value = LanguageRegistry.instance().getStringLocalization(getKey());
        
        if (value == null || value.isEmpty()) {
            return enUsDefault;
        }
        
        return value;
    }
    
}
