package com.startandroid.gyroscope;
import java.util.LinkedList;
import java.util.Queue;

public class GyroQueue {
    public GyroQueue() {
        queue = new LinkedList<TData>();
    }

    // добавление элемента
    public boolean Offer( TData data ) {
        return queue.offer( data );

    }
    // извлечение элемента
    public TData Poll() {
        return queue.poll();
    }

    // -------- PRIVATE ---------------------
    private Queue<TData> queue;

}
