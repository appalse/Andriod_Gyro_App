package com.startandroid.gyroscope;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Acer-PC on 07.10.2015.
 */
public class GyroQueue {
    // Синглтон
    static public GyroQueue GetGyroQueue() {
        if( pGyroQueue == null ) {
            pGyroQueue = new GyroQueue();
        }
        return pGyroQueue;
    }

    // добавление элемента
    public boolean Offer(String name, double x, double y, double z) {
        return pGyroQueue.offer(name, x, y, z);

    }
    // извлечение элемента
    public TData Poll() {
        return pGyroQueue.poll();
    }

    // -------- PRIVATE ---------------------
    private Queue<TData> queue;

    static private GyroQueue pGyroQueue = null;
    private GyroQueue() {
        calendar = Calendar.getInstance();
        queue = new LinkedList<TData>();
    }
    private boolean offer( String name, double x, double y, double z) {
        Date now = calendar.getTime();
        Timestamp timeStamp = new Timestamp( now.getTime());
        TData data = new TData( name, timeStamp.toString(), x, y, z);
        return queue.offer(data);
    }
    private TData poll() {
        return queue.poll();
    }
    private Calendar calendar;
}
