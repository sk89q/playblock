package com.sk89q.mapquery;

import java.util.List;

public class ListAccessor implements PathSegment {

    private final int key;

    public ListAccessor(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }

    @Override
    public Object next(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof List) {
            List list = ((List) object);
            if (key < 0 || key >= list.size()) {
                return null;
            }
            return list.get(key);
        }
        return null;
    }

}