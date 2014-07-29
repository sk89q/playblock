package com.skcraft.playblock.queue;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

import org.apache.logging.log4j.Level;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.sk89q.forge.AbstractBehavior;
import com.sk89q.forge.BehaviorPayload;
import com.sk89q.forge.ResponseTracker;
import com.skcraft.playblock.PlayBlock;
import com.skcraft.playblock.media.InvalidLengthException;
import com.skcraft.playblock.media.Media;
import com.skcraft.playblock.network.BehaviorType;
import com.skcraft.playblock.network.Enqueue;
import com.skcraft.playblock.network.EnqueueResponse;
import com.skcraft.playblock.network.EnqueueResponse.Response;
import com.skcraft.playblock.player.MediaPlayerHost;

/**
 * Accepts requests to add media clips to the queue, as well as for managing the
 * queue and listing its contents.
 */
public class QueueBehavior extends AbstractBehavior {

    private final MediaPlayerHost host;

    /**
     * Create a new queue behavior.
     * 
     * @param host
     *            the host, or null if it is the client
     */
    public QueueBehavior(MediaPlayerHost host) {
        this.host = host;
    }

    /**
     * Get the current media queue of the host.
     * 
     * @return the media queue, or null if the host is not in queue mode
     */
    private MediaQueue getQueue() {
        return host.getQueue();
    }

    /**
     * Submit a URI to be added to the queue.
     * 
     * @param uri
     *            the URI
     * @return a future
     */
    public ListenableFuture<EnqueueResponse> sendEnqueueRequest(String uri) {
        Enqueue enqueue = new Enqueue();
        enqueue.setUri(uri);

        ListenableFuture<EnqueueResponse> future = PlayBlock.getRuntime().getTracker().track(enqueue);

        firePayloadSend(new BehaviorPayload(BehaviorType.ENQUEUE, enqueue), null);

        return future;
    }

    /**
     * Send a response to an queue add attempt.
     * 
     * @param request
     *            the original payload with the request to enqueue
     * @param player
     *            the player to send it to
     * @param type
     *            the type of response
     * @param media
     *            the media clip, or null
     */
    protected void sendEnqueueResponse(Enqueue request, EntityPlayer player, Response type, Media media) {
        EnqueueResponse response = new EnqueueResponse(type, media);
        ResponseTracker.markResponseFor(request, response);
        List<EntityPlayer> players = new ArrayList<EntityPlayer>();
        players.add(player);
        firePayloadSend(new BehaviorPayload(BehaviorType.ENQUEUE_RESULT, response), players);
    }

    /**
     * Read an {@link Enqueue}.
     * 
     * @param player
     *            the player
     * @param enqueue
     *            the payload
     */
    private void handleClientEnqueue(final EntityPlayer player, final Enqueue enqueue) {
        MediaQueue queue = getQueue();

        // Check to see whether we have a queue
        if (queue == null) {
            sendEnqueueResponse(enqueue, player, Response.ERROR_NO_QUEUE, new Media(enqueue.getUri()));
            return;
        }

        ListenableFuture<Media> future = PlayBlock.getRuntime().getQueueSupervisor().submit(queue, enqueue.getUri());

        // Add a callback
        Futures.addCallback(future, new FutureCallback<Media>() {
            @Override
            public void onSuccess(Media result) {
                sendEnqueueResponse(enqueue, player, Response.OK, result);
            }

            @Override
            public void onFailure(Throwable t) {
                Media media = new Media(enqueue.getUri());

                if (t instanceof InvalidLengthException) {
                    sendEnqueueResponse(enqueue, player, Response.ERROR_UNKNOWN_LENGTH, media);
                } else {
                    PlayBlock.log(Level.INFO, "Failed to enqueue " + enqueue.getUri() + " from " + player.getCommandSenderName(), t);
                    sendEnqueueResponse(enqueue, player, Response.ERROR_INTERNAL, media);
                }
            }
        });
    }

    @Override
    public void readPayload(EntityPlayer player, BehaviorPayload payload, DataInputStream in) throws IOException {

        // Server
        if (host != null) {
            if (payload.isType(BehaviorType.ENQUEUE)) {
                Enqueue enqueue = new Enqueue();
                enqueue.read(in);

                handleClientEnqueue(player, enqueue);
            }

            // Client
        } else {
            if (payload.isType(BehaviorType.ENQUEUE_RESULT)) {
                EnqueueResponse response = new EnqueueResponse();
                response.read(in);

                PlayBlock.getRuntime().getTracker().fireFuture(response);
            }
        }
    }

}
