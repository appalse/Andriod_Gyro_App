package com.startandroid.gyroscope;
import java.io.OutputStream;
import java.net.Socket;

// This class is sending the data from specified GyroDataQueue to the connected client
// via specified socket.
// If we need to stop sending data the StopSending() method should be called.
// This class runs sending procedure in separate thread.


public class Sender {

    public Sender(Socket _socket, Logger _logger, QueuesHolder _queuesHolder ) {
        logger = _logger;
        OutputStream os = null;
        try {
            os = _socket.getOutputStream();
            Object mutex = new Object();
            senderInThread = new SenderInThread(logger, os, _queuesHolder, mutex);
            senderThread = new Thread(senderInThread);
            senderThread.start();
        } catch(Exception e) {
            logger.WriteLine( e.getMessage(), getCurrentThreadName(), getClassName(), "startSending" );
        }
    }

    // Stop sending data to the client
    public void StopSending() {
        try {
            senderInThread.Stop();
            senderThread.join();
            senderThread = null;
        }  catch(Exception e) {
            logger.WriteLine( e.getMessage(), getCurrentThreadName(), getClassName(), "StopListenning" );
        }
    }

    // -------- PRIVATE ---------------------
    private Logger logger;
    private SenderInThread senderInThread;
    private Thread senderThread;

    private String getCurrentThreadName() {
        return Thread.currentThread().getName();
    }

    private String getClassName() {
        return this.getClass().getName();
    }

}
