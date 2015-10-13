package com.startandroid.gyroscope;
import java.util.List;
import java.util.ArrayList;

public class QueuesContainer {
    public QueuesContainer() {
        queuesList = new ArrayList<GyroQueue>();
    }

    public synchronized int AddQueue( GyroQueue gyroQueue ) {
        int indexOfAddedQueue = queuesList.size();
        queuesList.add( gyroQueue );
        return indexOfAddedQueue;
    }

    public synchronized void AddDataToQueues( TData data ) {
        for( int i = 0; i < queuesList.size(); ++i ) {
            queuesList.get(i).Offer(data);
        }
    }

    public synchronized GyroQueue GetQueue( int indexOfQueue ) {
        if( indexOfQueue >= queuesList.size() || indexOfQueue < 0 ) {
            return null;
        } else {
            return queuesList.get( indexOfQueue );
        }
    }

    // -------- PRIVATE ---------------------

    private List<GyroQueue> queuesList;
}
