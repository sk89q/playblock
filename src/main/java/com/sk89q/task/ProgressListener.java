package com.sk89q.task;

/**
 * Handles events from a task.
 */
public interface ProgressListener {

    /**
     * Called when the progress changes.
     * 
     * @param progress
     *            a value between 0 or 1, or -1 for indeterminate
     */
    void progressChange(double progress);

    /**
     * Called when the status changes.
     * 
     * @param message
     *            the new status message
     */
    void statusChange(String message);

    /**
     * Called when the task completes.
     */
    void complete();

    /**
     * Called when the task ends in an error.
     * 
     * @param exception
     *            the exception
     */
    void error(Throwable exception);

    /**
     * Called when the task has been aborted.
     */
    void aborted();

}
