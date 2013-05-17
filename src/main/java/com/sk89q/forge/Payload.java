package com.sk89q.forge;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Reads and writes raw bytes.
 * 
 * <p>Instances of payload are meant to be encapsulated in a container protocol that
 * can do the required signaling to make sure payloads end up at the right
 * destinations.</p>
 */
public interface Payload {

    /**
     * Read data from the given input stream.
     * 
     * @param in the input stream
     * @throws IOException on I/O error
     */
    void read(DataInputStream in) throws IOException;

    /**
     * Write data to the given output stream.
     * 
     * @param out the output stream
     * @throws IOException on I/O error
     */
    void write(DataOutputStream out) throws IOException;
    
}
