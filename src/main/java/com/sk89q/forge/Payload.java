package com.sk89q.forge;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface Payload {

    void read(DataInputStream in) throws IOException;
    
    void write(DataOutputStream out) throws IOException;
}
