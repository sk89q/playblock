package com.skcraft.playblock.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * A very simple and small HTTP client.
 */
public class HttpRequest {

    public static enum Method {
        GET, POST
    };

    private String userAgent = "PlayBlock";
    private int readTimeout = 4000;
    private int bufferSize = 1024 * 8;
    private Method method = Method.GET;
    private String url;

    private final List<Entry<String, String>> queryParams = new ArrayList<Entry<String, String>>();
    private final List<Entry<String, String>> postParams = new ArrayList<Entry<String, String>>();

    public HttpRequest(String url) {
        Validate.notNull(url);
        this.url = url;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public void addQueryParam(String key, String value) {
        Validate.notNull(key);
        Validate.notNull(value);
        queryParams.add(new SimpleImmutableEntry<String, String>(key, value));
    }

    public void addPostParam(String key, String value) {
        Validate.notNull(key);
        Validate.notNull(value);
        postParams.add(new SimpleImmutableEntry<String, String>(key, value));
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        Validate.notNull(url);
        this.url = url;
    }

    private URL buildUrl() throws MalformedURLException {
        String query = buildQuery(queryParams);
        return new URL(url + (!query.isEmpty() ? "?" + query : ""));
    }

    public void read(OutputStream out) throws IOException {
        Validate.notNull(out);

        HttpURLConnection conn = null;
        InputStream in = null;
        byte[] postData = null;

        if (postParams.size() > 0) {
            postData = buildQuery(postParams).getBytes("UTF-8");
        }

        try {
            conn = (HttpURLConnection) buildUrl().openConnection();
            conn.setRequestProperty("User-Agent", userAgent);
            conn.setReadTimeout(readTimeout);
            conn.setRequestMethod(method.name());

            // Make sure to set the headers
            if (postData != null) {
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postData.length));
                conn.setUseCaches(false);
                conn.setDoInput(true);
            }

            conn.setDoOutput(true);
            conn.connect();

            // Write POST data
            if (postData != null) {
                OutputStream postOut = conn.getOutputStream();
                postOut.write(postData);
                postOut.flush();
                postOut.close();
            }

            in = new BufferedInputStream(conn.getInputStream());

            byte[] data = new byte[bufferSize];
            int len = 0;

            while ((len = in.read(data, 0, bufferSize)) >= 0) {
                out.write(data, 0, len);
            }
        } finally {
            close(in);
        }
    }

    public String getText(Charset charset) throws IOException {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(byteArray);
        try {
            read(bos);
            bos.flush();
        } finally {
            close(bos);
        }
        return new String(byteArray.toByteArray(), charset);
    }

    public String readText() throws IOException {
        return getText(Charset.forName("UTF-8"));
    }

    private static String buildQuery(List<Entry<String, String>> params) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;

        for (Entry<String, String> param : params) {
            if (!first) {
                builder.append("&");
            }

            builder.append(urlEncode(param.getKey()));
            builder.append("=");
            builder.append(urlEncode(param.getValue()));

            first = false;
        }

        return builder.toString();
    }

    private static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
            }
        }
    }

}
