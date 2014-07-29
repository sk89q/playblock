package com.skcraft.playblock.queue;

import java.util.Timer;

/**
 * Manages media queues and fires callbacks.
 */
public class QueueManager {

    private Timer queueTimer = new Timer("PlayBlock Queue Tracker", true);

    /**
     * Create a new media queue.
     * 
     * @return the new media queue
     */
    public MediaQueue createQueue() {
        return new MediaQueue(this);
    }

    /**
     * Get the queue timer.
     * 
     * @return the queue timer
     */
    Timer getQueueTimer() {
        return queueTimer;
    }

}
