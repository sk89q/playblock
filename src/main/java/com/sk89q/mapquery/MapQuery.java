package com.sk89q.mapquery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minidev.json.JSONValue;
import net.minidev.json.parser.ParseException;

/**
 * Fast way to access {@link Map}s.
 */
public class MapQuery {

    private static final String DELIMETER = "(?<!\\\\)\\.";
    private static final Pattern ARRAY_SYNTAX = Pattern.compile("^([0-9]+)$");

    private final Map<Object, Object> root;

    public MapQuery() {
        this(new HashMap<Object, Object>());
    }

    public MapQuery(Map<Object, Object> root) {
        this.root = root;
    }

    public Object get(PathSegment... parts) {
        Object parent = root;
        for (int i = 0; i < parts.length; i++) {
            Object v = parts[i].next(parent);
            if (parts.length == i + 1) {
                return v;
            }
            parent = v;
        }
        return null;
    }

    public PathSegment[] parsePath(String path) {
        List<PathSegment> segments = new ArrayList<PathSegment>();
        for (String token : path.split(DELIMETER)) {
            token = token.replaceAll("\\\\", "");
            Matcher m;

            // Array access
            m = ARRAY_SYNTAX.matcher(token);
            if (m.matches()) {
                segments.add(new ListAccessor(Integer.parseInt(m.group(1))));
            } else {
                segments.add(new MapAccessor(token));
            }
        }

        PathSegment[] segmentsArr = new PathSegment[segments.size()];
        segments.toArray(segmentsArr);
        return segmentsArr;
    }

    public boolean containsPath(String path) {
        return get(parsePath(path)) != null;
    }

    public Object get(String path) {
        return get(parsePath(path));
    }

    public <T> T getOf(String path, Class<T> type) {
        Object v = get(path);
        if (v == null) {
            return null;
        }
        if (type.isAssignableFrom(v.getClass())) {
            return (T) v;
        } else {
            return null;
        }
    }

    public String getString(String path) {
        return getOf(path, String.class);
    }

    public Integer getInt(String path) {
        return getOf(path, Integer.class);
    }

    public Long getLong(String path) {
        return getOf(path, Long.class);
    }

    public Float getFloat(String path) {
        return getOf(path, Float.class);
    }

    public Double getDouble(String path) {
        return getOf(path, Double.class);
    }

    public MapQuery wrapMapQuery(String path) {
        Map v = getOf(path, Map.class);
        if (v == null) {
            return new MapQuery();
        }
        return new MapQuery(v);
    }

    public static MapQuery fromJson(String data) throws IOException {
        try {
            Object value = JSONValue.parseWithException(data);
            if (value instanceof Map) {
                return new MapQuery((Map<Object, Object>) value);
            } else {
                throw new IOException("Provided JSON data is not an JSON object");
            }
        } catch (ParseException e) {
            throw new IOException("Failed to JSON data", e);
        }
    }

    public static MapQuery fromJsonApi(String data, String errorPath) throws IOException {
        MapQuery query = fromJson(data);
        Object errorString = query.get(errorPath);
        if (errorString != null) {
            throw new IOException("API provided an error: " + String.valueOf(errorString));
        }
        return query;
    }

}
