package com.skcraft.playblock.queue;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.skcraft.playblock.media.InvalidLengthException;
import com.skcraft.playblock.media.Media;
import com.skcraft.playblock.media.MediaResolver;

public class SimpleQueueSupervisor implements QueueSupervisor {

    private final Executor executor = Executors.newCachedThreadPool();
    private final MediaResolver resolver;

    public SimpleQueueSupervisor(MediaResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public ListenableFuture<Media> submit(MediaQueue queue, String uri) {
        SettableFuture<Media> future = SettableFuture.create();
        QueueSubmitter submitter = new QueueSubmitter(future, queue, uri);
        executor.execute(submitter);
        return future;
    }

    private class QueueSubmitter implements Runnable {

        private final SettableFuture<Media> future;
        private final MediaQueue queue;
        private final String uri;

        private QueueSubmitter(SettableFuture<Media> future, MediaQueue queue, String uri) {
            this.future = future;
            this.queue = queue;
            this.uri = uri;
        }

        @Override
        public void run() {
            try {
                Media media = resolver.lookup(uri);
                if (media.getLength() == null || media.getLength() <= 0) {
                    throw new InvalidLengthException();
                }
                queue.add(media);
                future.set(media);
            } catch (QueueException t) {
                future.setException(t);
            } catch (IOException t) {
                future.setException(t);
            }
        }

    }

}
