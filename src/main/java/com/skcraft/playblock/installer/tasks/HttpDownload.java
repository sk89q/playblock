package com.skcraft.playblock.installer.tasks;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.sk89q.task.Task;
import com.sk89q.task.TaskException;
import com.skcraft.playblock.util.IOUtils;

/**
 * Downloads a URL to a file.
 */
public class HttpDownload extends Task {

    private static final int READ_BUFFER_SIZE = 1024 * 8;
    private static final int READ_TIMEOUT = 1000 * 10;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) " + "Chrome/28.0.1468.0 Safari/537.36";

    private final String url;
    private final OutputStream out;

    public HttpDownload(String url, OutputStream out) {
        this.url = url;
        this.out = out;
    }

    @Override
    public void execute() throws TaskException, InterruptedException {
        HttpURLConnection conn = null;
        InputStream in = null;
        boolean error = false;

        try {
            fireProgressChange(0);

            URL urlObject = new URL(url);

            fireStatusChange("Connecting to " + urlObject.getHost() + "...");

            // Make a HTTP connection
            conn = (HttpURLConnection) urlObject.openConnection();
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.connect();

            if (Thread.interrupted()) {
                throw new InterruptedException();
            }

            fireStatusChange("Connected.");

            int length = conn.getContentLength();

            in = new BufferedInputStream(conn.getInputStream());

            if (conn.getResponseCode() == 200) {
                String message = "Downloaded %s from " + urlObject.getHost().replace("%", "%%") + "...";
                attach(new StreamCopy(in, out, length, message), 0, 1).execute();
            } else {
                throw new TaskException("Got response " + conn.getResponseCode() + ": " + conn.getResponseMessage() + ".");
            }

            conn.disconnect();

            fireStatusChange("Completed.");
            fireProgressChange(1);
        } catch (MalformedURLException e) {
            throw new TaskException("The URL '" + url + "' isn't recognized.", e);
        } catch (IOException e) {
            throw new TaskException("Failed to download: " + e.getMessage(), e);
        } finally {
            IOUtils.close(out);
            IOUtils.close(in);
        }
    }

}
