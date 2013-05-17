package com.skcraft.playblock.media;

import com.skcraft.playblock.queue.QueueException;

public class InvalidLengthException extends QueueException {

    public InvalidLengthException() {
    }

    public InvalidLengthException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidLengthException(String message) {
        super(message);
    }

    public InvalidLengthException(Throwable cause) {
        super(cause);
    }

}
