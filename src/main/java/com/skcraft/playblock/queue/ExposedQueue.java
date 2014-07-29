package com.skcraft.playblock.queue;

/**
 * Indicates an object that has a {@link MediaQueue} that can be controlled
 * externally.
 */
public interface ExposedQueue {

    /**
     * Get the underlying queue behavior.
     * 
     * @return the queue behavior
     */
    QueueBehavior getQueueBehavior();

}
