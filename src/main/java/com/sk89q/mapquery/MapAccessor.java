package com.sk89q.mapquery;

import java.util.Map;

public class MapAccessor implements PathSegment {

    private final Object key;

    public MapAccessor(Object key) {
        this.key = key;
    }

    public Object getKey() {
        return key;
    }

    @Override
    public Object next(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Map) {
            return ((Map) object).get(key);
        }
        return null;
    }

}