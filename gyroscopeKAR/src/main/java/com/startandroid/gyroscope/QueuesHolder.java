package com.startandroid.gyroscope;
import java.util.List;
import java.util.ArrayList;

// This class is a container for different queues with gyroscope and accelerometer data
// Different threads can use this container therefore its methods are synchronized

public class QueuesHolder {
    public QueuesHolder( Logger _logger ) {
        logger = _logger;
        queuesList = new ArrayList<GyroDataQueue>();
    }

    public synchronized GyroDataQueue AddNewQueue( Object mutex) {
        int indexOfAddedQueue = -1;
        GyroDataQueue newQueue = new GyroDataQueue(mutex);
        if( queuesList != null && newQueue != null ) {
            queuesList.add(newQueue);
            indexOfAddedQueue = queuesList.size() - 1;
        }
        if( indexOfAddedQueue < 0 ) {
            return null;
        } else {
            return newQueue;
        }
    }

    public synchronized void PushDataToQueues(TData data) {
        try {
           for( int i = 0; i < queuesList.size(); ++i ) {
                 queuesList.get(i).Offer(data);
           }
        } catch( Exception e ) {
            logger.WriteLine(e.getMessage(), this.getClass().getName(), "PushDataToQueues");
        }
    }

    // Delete all queues from queue list, when server is stopped
    public synchronized void DeleteAllQueues() {
         logger.WriteLine("Queue list is deleted " );
         queuesList.clear();
    }

    // Remove 1 queue from queue list, when client is disconnected
    public synchronized void RemoveQueue( GyroDataQueue dataQueue ) {
        if(queuesList != null && dataQueue != null ) {
            try {
                int indexOfQueue = queuesList.indexOf(dataQueue);
                if( indexOfQueue < 0 ) {
                    throw new Exception("Specified queue wasn't found");
                }
                queuesList.remove(indexOfQueue);
            } catch( Exception e ) {
                logger.WriteLine(e.getMessage(), getClassName(), "RemoveQueue" );
            }
        }
    }
    // -------- PRIVATE ---------------------
    private List<GyroDataQueue> queuesList = null;
    private Logger logger;

    private String getClassName() {
        return this.getClass().getName();
    }
}
