package com.skcraft.playblock.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import com.sk89q.forge.AbstractBehavior;


/**
 * Manages subscribing clients to a resource based on their distance away.
 */
public class DoubleThresholdRange extends AbstractBehavior {

    private static final int MAX_RANGE = 64;
    private static final int MIN_BUFFER_RANGE = 5;

    private float triggerRange = 0;
    private float fadeRange = MIN_BUFFER_RANGE;

    public DoubleThresholdRange() {
    }

    /**
     * Gets the range (in blocks) at which the player will activate and start playing.
     * 
     * @see #getTriggerRangeSq() get the squared version, which is faster
     * @return the range in blocks
     */
    public float getTriggerRange() {
        return Math.round(Math.sqrt(triggerRange) * 100 / 100);
    }

    /**
     * Gets the range (in blocks) at which the player will activate and start playing.
     * 
     * @return the range in blocks, squared
     */
    public float getTriggerRangeSq() {
        return triggerRange;
    }

    /**
     * Sets the range (in blocks) at which the player will activate and start playing.
     * 
     * @param range the range in blocks
     */
    public void setTriggerRange(float range) {
        float v = MathUtils.clamp(range, 1, MAX_RANGE);
        triggerRange = v * v; // Store values squared
        ensureProperBuffer();
    }

    /**
     * Gets the range (in blocks) at which the player will stop playing if it is
     * currently playing.
     * 
     * @see #getFadeRangeSq() get the squared version, which is faster
     * @return the range
     */
    public float getFadeRange() {
        return Math.round(Math.sqrt(fadeRange) * 100 / 100);
    }

    /**
     * Gets the range (in blocks) at which the player will stop playing if it is
     * currently playing.
     * 
     * @return the range in blocks, squared
     */
    public float getFadeRangeSq() {
        return fadeRange;
    }

    /**
     * Sets the range (in blocks) at which the player will stop playing if it is
     * currently playing.
     * 
     * @param range range in blocks, squared
     */
    public void setFadeRange(float range) {
        float v = MathUtils.clamp(range, 1, MAX_RANGE + MIN_BUFFER_RANGE);
        fadeRange = v * v; // Store values squared
        ensureProperBuffer();
    }
    
    /**
     * This changes the fade distance appropriately to ensure that there is at least
     * a {@value #MIN_BUFFER_RANGE} block distance difference between the trigger
     * distance and the fade distance.
     */
    private void ensureProperBuffer() {
        float min = getTriggerRange() + MIN_BUFFER_RANGE;
        if (getFadeRange() < min) {
            // Do not call setFadeRange()!
            fadeRange = min * min; // Store values squared
        }
    }

    /**
     * Return whether the distance given is within the trigger range.
     * 
     * @param distance the distance (squared)
     * @return true if within range
     */
    public boolean inTriggerRangeSq(double distance) {
        return distance <= getTriggerRangeSq();
    }

    /**
     * Return whether the distance given is within the fade range (the distance is
     * greater than the fade range).
     * 
     * @param distance the distance (squared)
     * @return true if within range
     */
    public boolean inFadeRangeSq(double distance) {
        return distance >= getFadeRangeSq();
    }
    
    /**
     * Create a local player range test.
     * 
     * @return the test
     */
    public RangeTest createRangeTest() {
        return new RangeTest();
    }
    
    /**
     * Write NBT data that needs to be saved to the world.
     * 
     * @param tag the tag
     */
    @Override
    public void writeSaveNBT(NBTTagCompound tag) {
        tag.setFloat("triggerRange", getTriggerRange());
        tag.setFloat("fadeRange", getFadeRange());
    }
    
    /**
     * Read NBT data that has been retrieved from a saved world.
     * 
     * @param tag the tag
     */
    @Override
    public void readSaveNBT(NBTTagCompound tag) {
        setTriggerRange(tag.getFloat("triggerRange"));
        setFadeRange(tag.getFloat("fadeRange"));
    }

    @Override
    public void writeNetworkedNBT(NBTTagCompound tag) {
        tag.setFloat("triggerRange", getTriggerRange());
        tag.setFloat("fadeRange", getFadeRange());
    }

    @Override
    public void readNetworkedNBT(NBTTagCompound tag) {
        if (tag.hasKey("triggerRange")) {
            setTriggerRange(tag.getFloat("triggerRange"));
            setFadeRange(tag.getFloat("fadeRange"));
        }
    }
    
    public class RangeTest {
        
        private int lastX = Integer.MAX_VALUE;
        private int lastY = Integer.MAX_VALUE;
        private int lastZ = Integer.MAX_VALUE;
        private boolean withinRange = false;
        
        private RangeTest() {}
        
        public boolean inRange(double x, double y, double z) {
            EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
            if (player.posX != lastX || player.posY != lastY || player.posZ != lastZ) {
                double distance = player.getDistanceSq(x, y, z);
                if (!withinRange && inTriggerRangeSq(distance)) {
                    withinRange = true;
                    return true;
                } else if (withinRange && inFadeRangeSq(distance)) {
                    withinRange = false;
                    return false;
                }
            }
            
            return withinRange;
        }
        
        public boolean getCachedInRange() {
            return withinRange;
        }
        
        
    }

}
