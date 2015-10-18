package com.startandroid.gyroscope;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

// This class is sending the data from specified GyroDataQueue to the connected client
// via specified socket.
// If we need to stop sending data the StopSending() method should be called.
// This class is run in separate thread.
// The variable 'boolean shouldSending' can be changed from other thread therefore it is synchronized

public class Sender  implements Runnable  {


    public Sender(Socket _socket, Logger _logger, QueuesHolder _queuesHolder ) {
        socket = _socket;
        logger = _logger;
        queuesHolder = _queuesHolder;
        shouldSending = new AtomicBoolean(false);
        // Create the queue with data (from gyroscope and accelerometer sensors)
        // The client will receive the data from this queue
        dataQueue = queuesHolder.AddNewQueue();
    }

    // Run thread.
    public void run()
    {
        logger.WriteLine("Hello from Sender thread!");
        if(dataQueue != null) {
            shouldSending.set(true);
            startSending();
        } else {
            logger.WriteLine("queuesHolder.AddNewQueue() cannot add queue!", getCurrentThreadName(), getClassName(), "run");
        }
    }

    // Stop sending data to the client
    public void StopSending() {
        shouldSending.set(false);
    }

    // -------- PRIVATE ---------------------
    private Logger logger;
    private Socket socket;
    private AtomicBoolean shouldSending;
    private GyroDataQueue dataQueue;
    private QueuesHolder queuesHolder;

    // Start sending gyroscope and accelerometer data from specified data queue to the client
    private void startSending() {
        try {
            logger.WriteLine("Sending is started!");
            TData data;
            String text;
            OutputStream os = socket.getOutputStream();
            while(shouldSending.get() ) {
                data = dataQueue.Poll();
                if( data != null ) {
                    text = data.getX() + ", " + data.getY() + ", " + data.getZ();
                    os.write(text.getBytes());
                    logger.WriteLine("Text was sent! dataQueue.Poll() : " + text);
                }
            }
        } catch(IOException e) {
            if( e.getMessage().contains("Connection reset by peer: socket write error")) {
                logger.WriteLine( Thread.currentThread().getName() + " was closed by client", getCurrentThreadName(), getClassName(), "waitForNewConnection"  );
                queuesHolder.RemoveQueue(dataQueue);
            } else {
                logger.WriteLine( "Sender IOException " + e.getMessage(), getCurrentThreadName(), getClassName(), "startSending" );
            }
        } catch(Exception e) {
            logger.WriteLine( e.getMessage(), getCurrentThreadName(), getClassName(), "startSending" );
        }
    }

    private String getCurrentThreadName() {
        return Thread.currentThread().getName();
    }

    private String getClassName() {
        return this.getClass().getName();
    }
}
