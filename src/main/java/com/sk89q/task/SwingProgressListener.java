package com.sk89q.task;

import javax.swing.SwingUtilities;

/**
 * Wraps a thread-unsafe listener and makes calls executed within the Swing
 * thread.
 */
public class SwingProgressListener implements ProgressListener {

    private final ProgressListener listener;

    public SwingProgressListener(ProgressListener listener) {
        this.listener = listener;
    }

    @Override
    public void progressChange(final double progress) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                listener.progressChange(progress);
            }
        });
    }

    @Override
    public void statusChange(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                listener.statusChange(message);
            }
        });
    }

    @Override
    public void complete() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                listener.complete();
            }
        });
    }

    @Override
    public void aborted() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                listener.aborted();
            }
        });
    }

    @Override
    public void error(final Throwable exception) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                listener.error(exception);
            }
        });
    }

}
