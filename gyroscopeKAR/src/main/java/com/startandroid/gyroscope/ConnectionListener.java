package com.startandroid.gyroscope;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.lang.Thread;

/**
 * Created by Acer-PC on 07.10.2015.
 */
public class ConnectionListener implements Runnable  {

    public ConnectionListener(int _port, Logger _logger, QueuesContainer _gyroDataQueuesContainer ) {
        port = _port;
        logger = _logger;
        gyroDataQueuesContainer = _gyroDataQueuesContainer;
    }

    public void run() {
        logger.LogDebug( this.getClass().getName().toString(), "Привет из потока ConnectionListener!");
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            while(true) {
                Socket fromClient = serverSocket.accept();	// в отдельный поток - уже в отдельном потоке
                logger.LogDebug(this.getClass().getName().toString(), "New connection was detected");
                int indexOfAddedQueue = 0;//gyroDataQueuesContainer.AddQueue( new GyroQueue() ); // to add new queue of gyroscope data for this connection
                sender = new Sender(fromClient, logger, gyroDataQueuesContainer, indexOfAddedQueue );
                Thread thr1 = new Thread(sender);
                thr1.setName("Sender thread");
                thr1.start();
            }
        } catch(IOException e) {
            logger.LogError(this.getClass().getName().toString(), " IOException " + e.getMessage());
        } catch(Exception e) {
            logger.LogError(this.getClass().getName().toString(), e.getMessage() );
        }
    }

    public void StopAllConnections() {
        if( sender != null ) {
            sender.StopSending();
        }
    }

    // -------- PRIVATE ---------------------
    private Logger logger;
    private int port;
    private Sender sender;
    private QueuesContainer gyroDataQueuesContainer;
}
