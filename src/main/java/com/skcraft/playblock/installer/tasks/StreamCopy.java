package com.skcraft.playblock.installer.tasks;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.sk89q.task.Task;

public class StreamCopy extends Task {

    private String format = "%s";
    private final InputStream in;
    private final OutputStream out;
    private final long totalLength;
    private long totalRead = 0;
    private int bufferSize = 1024 * 8;

    public StreamCopy(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
        this.totalLength = -1;
    }

    public StreamCopy(InputStream in, OutputStream out, long totalLength) {
        this.in = in;
        this.out = out;
        this.totalLength = totalLength;
    }

    public StreamCopy(InputStream in, OutputStream out, long totalLength, String format) {
        this.in = in;
        this.out = out;
        this.totalLength = totalLength;
        this.format = format;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    public void execute() throws IOException, InterruptedException {
        fireProgressChange(0);

        Thread thread = new Thread(new ProgressTracker(), ProgressTracker.class.getCanonicalName());

        if (totalLength <= 0) {
            fireProgressChange(-1);
        }

        try {
            thread.start();

            byte[] data = new byte[bufferSize];
            int len = 0;

            while ((len = in.read(data, 0, bufferSize)) >= 0) {
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
                out.write(data, 0, len);
                totalRead += len;
            }
        } finally {
            thread.interrupt();
        }
    }

    /**
     * Fires progress events periodically.
     */
    private class ProgressTracker implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    update();
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
            }
        }

        private void update() {
            double readKBytes = Math.round(totalRead / 1024.0 * 100) / 100;

            if (totalLength <= 0) {
                // Fire status message
                String status = String.format("%,.2f kB", readKBytes);
                fireStatusChange(String.format(format, status));
            } else {
                double totalKBytes = Math.round(totalLength / 1024.0 * 100) / 100;

                // Fire progress percent
                double progress = Math.min(1, totalRead / (double) totalLength);
                fireProgressChange(progress);

                // Fire status message
                String status = String.format("%.0f%% (%,.0f kB)", progress * 100, readKBytes);
                fireStatusChange(String.format(format, status));
            }
        }

    }

}
