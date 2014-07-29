package com.sk89q.forge;

/**
 * Indicates an object that is part of a request-response that must result in a
 * callback when the response is received (once).
 * 
 * @param <T>
 *            the object to provide in the callback
 */
public interface RequestResponse<T> {

    /**
     * Get the internal call ID.
     * 
     * @return the id
     */
    short getCallId();

    /**
     * Set the internal call ID.
     * 
     * @param id
     *            the id
     */
    void setCallId(short id);

}
