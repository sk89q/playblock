package com.skcraft.playblock;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

import com.skcraft.playblock.PlayBlock;

import cpw.mods.fml.common.Loader;

public class SharedConfiguration {

    private File options;
    private Properties props;

    public SharedConfiguration(String fileName) {
        File configDir = Loader.instance().getConfigDir();
        props = new Properties();
        try {
            options = new File(configDir, fileName);
            props.load(new FileInputStream(options));
        }
        catch(FileNotFoundException e) {
            PlayBlock.log(Level.WARNING, "Failed to find " + options.getName());
        }
        catch(IOException e) {
            PlayBlock.log(Level.WARNING, "Failed to load " + options.getName());
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
            PlayBlock.log(Level.WARNING, "Failed to find " + options.getName());
        }
        catch(IOException e) {
            PlayBlock.log(Level.WARNING, "Failed to save " + options.getName());
        }
    }
    
    /**
     * Gets an integer.
     * 
     * @param key
     * @param def
     * @return the value
     */
    public int getInt(String key, int def) {
        String value = props.getProperty(key);
        if(value != null) {
            try {
                return Integer.parseInt(value);
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
