package com.sk89q.forge;

import java.util.HashMap;
import java.util.Map;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

public class ResponseTracker {

    private final Map<Short, SettableFuture<?>> futures = new HashMap<Short, SettableFuture<?>>();
    private short nextCallId = 0;

    public ResponseTracker() {
    }

    /**
     * Track a {@link RequestResponse}. Called from the original sending side.
     * 
     * @param trackable
     *            the trackable object
     * @return a future to assign callbacks to
     */
    public synchronized <T> ListenableFuture<T> track(RequestResponse<T> trackable) {
        SettableFuture<T> future = SettableFuture.create();
        futures.put(nextCallId, future);
        trackable.setCallId(nextCallId);
        nextCallId++;
        return future;
    }

    /**
     * Called after receiving a response, on the original sending side, in order
     * to fire the callbacks.
     * 
     * @param trackable
     *            the trackable object
     */
    public synchronized <T> void fireFuture(RequestResponse<T> trackable) {
        SettableFuture<T> future = (SettableFuture<T>) futures.remove(trackable.getCallId());

        if (future != null) {
            future.set((T) trackable);
        }
    }

    /**
     * Called on the receiving side that will issue the response, in order to
     * copy the call ID from the request to the response.
     * 
     * @param request
     *            the request
     * @param response
     *            the response
     */
    public static void markResponseFor(RequestResponse<?> request, RequestResponse<?> response) {
        response.setCallId(request.getCallId());
    }

}
