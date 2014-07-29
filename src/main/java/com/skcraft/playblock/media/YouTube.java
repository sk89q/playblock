package com.skcraft.playblock.media;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sk89q.mapquery.MapQuery;
import com.skcraft.playblock.util.HttpRequest;

/**
 * Looks up information about YouTube URIs.
 */
public class YouTube implements MediaProvider {

    private static final String API_URL = "https://www.googleapis.com/youtube/v3/";

    /**
     * API key for accessing YouTube, managed by sk89q.
     */
    private static final String API_KEY = "AIzaSyC8boYCJH5S5Z5pTuwx6HIUWYGeRZh-MOs";

    /**
     * Basic regex for parsing ISO-8601 durations.
     */
    private static final Pattern LENGTH_PATTERN = Pattern.compile("^PT(?:([0-9]+)H)?(?:([0-9]+)M)?(?:([0-9]+)S)?$", Pattern.CASE_INSENSITIVE);

    /**
     * Regex for matching YouTube links (including youtu.be links).
     */
    private static final Pattern urlPattern = Pattern.compile("^https?://(?:www\\.)?" + // https://www.
                                                                                        // portion
            "(?:youtube\\.com/+watch\\?.*(?<=[&\\?])v=|youtu\\.be/+)" + // Prefix
            "([A-Za-z0-9_\\-]+).*$", // Video ID
            Pattern.CASE_INSENSITIVE);

    @Override
    public int getConfidence(String uri) {
        return urlPattern.matcher(uri).matches() ? DEFAULT_CONFIDENCE : NO_CONFIDENCE;
    }

    @Override
    public Media lookup(String uri) throws IOException {
        Matcher m = urlPattern.matcher(uri);
        if (!m.matches()) {
            return new Media(uri); // Empty
        }

        return queryVideo(m.group(1));
    }

    private Media queryVideo(String id) throws IOException {
        HttpRequest request = new HttpRequest(API_URL + "videos");
        request.addQueryParam("key", API_KEY);
        request.addQueryParam("id", id);
        request.addQueryParam("part", "snippet,contentDetails,status");

        String data = request.readText();
        MapQuery result = MapQuery.fromJsonApi(data, "error.message");
        MapQuery entry = result.wrapMapQuery("items.0");

        // Video is missing?
        if (!entry.containsPath("id")) {
            return null;
        }

        Media info = new Media("http://youtube.com/watch?v=" + entry.getString("id"));
        info.setTitle(entry.getString("snippet.title"));
        info.setDescription(entry.getString("snippet.description"));
        info.setLength(parseLength(entry.getString("contentDetails.duration")));

        return info;
    }

    private Long parseLength(String text) {
        if (text == null) {
            return null;
        }

        // Example: "PT2M58S" -- ISO-8601
        // Not really a compliant parser
        Matcher m = LENGTH_PATTERN.matcher(text);
        if (m.matches()) {
            String hr = m.group(1);
            String min = m.group(2);
            String sec = m.group(3);

            long duration = 0;
            if (hr != null)
                duration += Long.parseLong(hr) * 60 * 60;
            if (min != null)
                duration += Long.parseLong(min) * 60;
            if (sec != null)
                duration += Long.parseLong(sec);
            return duration * 1000; // Milliseconds
        }

        return null;
    }

    public Media[] search(String query, int startIndex, int maxResults) throws IOException {
        HttpRequest request = new HttpRequest(API_URL + "search");
        request.addQueryParam("part", "id,snippet");
        request.addQueryParam("maxResults", Integer.toString(maxResults));
        request.addQueryParam("order", "relevance");
        request.addQueryParam("q", query);
        request.addQueryParam("type", "video");
        request.addQueryParam("key", API_KEY);

        String data = request.readText();
        MapQuery result = MapQuery.fromJsonApi(data, "error.message");
        Media[] results = new Media[maxResults];

        for (int i = 0; i < maxResults; i++) {
            MapQuery entry = result.wrapMapQuery("items." + i);
            Media videoResult = new Media("http://youtube.com/watch?v=" + entry.getString("id.videoId"));
            videoResult.setTitle(entry.getString("snippet.title"));
            videoResult.setThumbnail(entry.getString("snippet.thumbnails.default.url"));
            videoResult.setCreator(entry.getString("snippet.channelTitle"));
            results[i] = videoResult;
        }

        return results;
    }

}
