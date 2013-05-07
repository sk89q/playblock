package com.skcraft.playblock.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

import com.skcraft.playblock.PlayBlock;

import cpw.mods.fml.common.Loader;

public class ClientOptions {

    private File options;
    private Properties props;

    public ClientOptions() {
        File configDir = Loader.instance().getConfigDir();
        props = new Properties();
        try {
            options = new File(configDir, "PlayBlockSettings.txt");
            if(options.createNewFile()) {
                props.setProperty("volume", String.valueOf(1));
            }
            props.load(new FileInputStream(options));
            save();
        }
        catch(FileNotFoundException e) {
            PlayBlock.log(Level.WARNING, "Failed to find the client options " +
            		"file");
        }
        catch(IOException e) {
            PlayBlock.log(Level.WARNING, "Failed to load client settings");
        }
    }
    
    /**
     * Save the properties to a file.
     */
    public void save() {
        try {
            props.store(new FileOutputStream(options), null);
        }
        catch(FileNotFoundException e) {
            PlayBlock.log(Level.WARNING, "Failed to find client options file");
        }
        catch(IOException e) {
            PlayBlock.log(Level.WARNING, "Failed to save client settings");
        }
    }
    
    /**
     * Gets a float value.
     * 
     * @param key 
     * @param def
     * @return the value 
     */
    public float getFloat(String key, float def) {
        String value = props.getProperty(key);
        if(value != null) {
            try {
                return Float.parseFloat(value);
            }
            catch(NumberFormatException e) {
                props.setProperty(key, String.valueOf(def));
                return def;
            }
        }
        else {
            props.setProperty(key, String.valueOf(def));
            return def;
        }
    }
    
    /**
     * Sets a float value and saves it to the properties file.
     * 
     * @param key
     * @param value
     */
    public void setFloat(String key, float value) {
        props.setProperty(key, String.valueOf(value));
        save();
    }
}
