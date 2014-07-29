package com.sk89q.task;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingWorker;

/**
 * A task that can be performed in another thread, similar to
 * {@link SwingWorker}.
 */
public abstract class Task implements Runnable {

    private final List<ProgressListener> listeners = new ArrayList<ProgressListener>();

    private Thread thread;

    /**
     * Add a progress listener.
     * 
     * @param listener
     *            the listener
     */
    public final void addProgressListener(ProgressListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove a progress listener.
     * 
     * @param listener
     *            the listener
     */
    public final void removeProgressListener(ProgressListener listener) {
        listeners.remove(listener);
    }

    /**
     * Called when the progress changes.
     * 
     * @param progress
     *            a value between 0 or 1, or -1 for indeterminate
     */
    protected final void fireProgressChange(final double progress) {
        for (ProgressListener listener : listeners) {
            listener.progressChange(progress);
        }
    }

    /**
     * Called when the status changes.
     * 
     * @param message
     *            the new status message
     */
    protected final void fireStatusChange(final String message) {
        for (ProgressListener listener : listeners) {
            listener.statusChange(message);
        }
    }

    /**
     * Called when the task completes.
     */
    private void fireComplete() {
        for (ProgressListener listener : listeners) {
            listener.complete();
        }
    }

    /**
     * Called when the task ends in an error.
     * 
     * @param exception
     *            the exception
     */
    private void fireError(final Throwable exception) {
        for (ProgressListener listener : listeners) {
            listener.error(exception);
        }
    }

    /**
     * Called when the task has been aborted.
     */
    private void fireAborted() {
        for (ProgressListener listener : listeners) {
            listener.aborted();
        }
    }

    /**
     * Attach a listener to a given task in order to redirect events to this
     * task.
     * 
     * @param task
     *            the sub-task
     * @param lower
     *            the lower bound for the progress events
     * @param upper
     *            the upper bound for the progress events
     */
    protected <T extends Task> T attach(T task, final double lower, final double upper) {
        ProgressListener listener = new ProgressListener() {
            @Override
            public void statusChange(String message) {
                fireStatusChange(message);
            }

            @Override
            public void progressChange(double progress) {
                if (progress < 0) {
                    fireProgressChange(-1);
                } else {
                    fireProgressChange((upper - lower) * progress + lower);
                }
            }

            @Override
            public void error(Throwable exception) {
            }

            @Override
            public void complete() {
            }

            @Override
            public void aborted() {
            }
        };

        task.addProgressListener(listener);
        return task;
    }

    /**
     * Run a task in another thread.
     */
    public final void start() {
        Thread thread = new Thread(this, getClass().getCanonicalName());
        this.thread = thread;
        thread.start();
    }

    /**
     * Attempts to cancel the task by throwing an {@link InterruptedException}
     * in the thread.
     * 
     * <p>
     * This should not be called if this task is not running in a different
     * thread. In addition, this method is only usable if the task was started
     * with {@link #start()} and not with an external executor.
     * </p>
     * 
     * <p>
     * It is still possible for the task to complete or error after the task has
     * been cancelled.
     * </p>
     */
    public void cancel() {
        if (thread != null) {
            thread.interrupt();
        }
    }

    @Override
    public final void run() {
        try {
            execute();
            fireComplete();
        } catch (InterruptedException t) {
            handleCancel();
            fireAborted();
        } catch (Throwable t) {
            fireError(t);
        }
    }

    /**
     * Called to perform the task.
     */
    protected abstract void execute() throws Exception;

    /**
     * Called if the task has been cancelled.
     */
    protected void handleCancel() {
    }

}
