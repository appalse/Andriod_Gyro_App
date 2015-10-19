package com.startandroid.gyroscope;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

// This class keeps the queue with data.
// The data can be pushed by main thread, when gyroscope or accelerometer sensor were changed.
// The data can be extracted by ServerSocket (some other thread) to pass them to the client
// (via TCP/IP connection).
// As the data are extracted and are pushed from different threads the synchronization is used.
// We synchronize both methods (Poll and Offer) and queue with data (usage of BlockingQueue)

public class GyroDataQueue {
    // Create queue with data
    public GyroDataQueue(Object _mutex) {
        queueWithData = new LinkedBlockingQueue<TData>();
        mutex = _mutex;
    }

    // Add new element to queue
    public boolean Offer( TData data ) {
        boolean result = false;
        synchronized( this ) {
            result = queueWithData.offer(data);
        }
        synchronized( mutex ) {
            mutex.notify();
        }
        return result;
    }
    // Extract element from queue. If cannot extract data, the null is returned
    public synchronized TData Poll() {
        return queueWithData.poll();
    }

    public synchronized boolean HasData() {
        return queueWithData.size() > 0;
    }

    // -------- PRIVATE ---------------------
    private BlockingQueue<TData> queueWithData;
    private Object mutex;

}
