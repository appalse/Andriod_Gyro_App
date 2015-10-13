package com.startandroid.gyroscope;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;


/**
 * Created by Acer-PC on 07.10.2015.
 */
public class Sender  implements Runnable  {


    public Sender(Socket _socket, Logger _logger, QueuesContainer _gyroDataQueuesContainer, int _indexOfAddedQueue ) {
        socket = _socket;
        logger = _logger;
        gyroDataQueuesContainer = _gyroDataQueuesContainer;
        indexOfAddedQueue = _indexOfAddedQueue;
    }

    public void run()
    {
        logger.LogDebug(this.getClass().getName().toString(), "Привет из потока Sender!");
        send();
    }

    public void start()   {
        logger.LogDebug(this.getClass().getName().toString(), "start Sender!");
    }

    public void StopSending() {
        logger.LogDebug(this.getClass().getName().toString(), "stop Sender!");
        isRunning = false;
    }

    // -------- PRIVATE ---------------------

    private Logger logger;
    private Socket socket;
    private volatile boolean isRunning = true;
    private QueuesContainer gyroDataQueuesContainer;
    private int indexOfAddedQueue;

    private void send() {
        try {
            GyroQueue gyroQueue = gyroDataQueuesContainer.GetQueue(indexOfAddedQueue);
            logger.LogDebug(this.getClass().getName().toString(), "Server is started");
            int i = 0;
            TData data;
            String text;
            while(isRunning) {
                OutputStream os = socket.getOutputStream();
                data = gyroQueue.Poll();
                if( data != null ) {
                    text = data.getSensorName() + " " + data.getTime() + " : " + data.getX() + ", " + data.getY() + ", " + data.getZ() + ".";
                    os.write(text.getBytes());
                    logger.LogDebug(this.getClass().getName().toString(), "Poll " + data.getX() + ", " + data.getY() + ", " + data.getZ());
                    logger.LogDebug(this.getClass().getName().toString(), "Text was sent!");
                }
            }
        } catch(IOException e) {
            logger.LogError(this.getClass().getName().toString(), "Sender IOException" + e.getMessage());
        } catch(Exception e) {
            logger.LogError(this.getClass().getName().toString(),  e.getMessage() );
        }
    }
}
