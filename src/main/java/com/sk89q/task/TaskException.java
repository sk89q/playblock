package com.sk89q.task;

public class TaskException extends RuntimeException {

    private final String log;

    public TaskException() {
        this.log = null;
    }

    public TaskException(String message) {
        super(message);
        this.log = null;
    }

    public TaskException(String message, String log) {
        super(message);
        this.log = log;
    }

    public TaskException(String message, Throwable t) {
        super(message, t);
        this.log = null;
    }

    public TaskException(String message, Throwable t, String log) {
        super(message, t);
        this.log = log;
    }

}
