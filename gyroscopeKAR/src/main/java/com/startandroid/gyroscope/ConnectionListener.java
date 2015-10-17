package com.startandroid.gyroscope;
// This class is a container for ServerSocket's connections.
// It is run in separate thread and it is listening for new connection to the server via specified port

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConnectionListener implements Runnable  {

    public ConnectionListener(Logger _logger, int _portN, QueuesHolder _queuesHolder) {
        logger = _logger;
        portN = _portN;
        queuesHolder = _queuesHolder;
        shouldWaiting = new AtomicBoolean(false);
    }

    // The connection listening is run in separate thread
    public void run() {
        logger.WriteLine("Hello from the ConnectionListener thread!");
        try{
            // Create ServerSocket
            serverSocket = new ServerSocket(portN);
            shouldWaiting.set(true);
            // Create array for Sender objects which are responsible for sending data to the client
            // The new Sender is created (in new thread) after establishing a connection between ServerSocket and client
            senders = new ArrayList<Sender>();
            waitForNewConnections();
        } catch( Exception e ) {
            String message = "Error while creating ServerSocket" +e.getMessage();
            logger.WriteLine( message, getCurrentThreadName(), getClassName(), "ConnectionListener" );
            serverSocket = null;
        }

    }

    // stop listening for connection, clear array with senders and close server
    // In other words, stop all connections, clear all data.
    public void StopListenning() {
        shouldWaiting.set(false);
        try {
            // firstly, stop sending data from existing servers (ServerSockets -> client's Sockets)
            for( int i = 0; i < senders.size(); ++i ) {
                senders.get(i).StopSending();
            }
            // clear container
            senders.clear();
            senders = null;
            // secondly, close ServerSocket
            if( serverSocket != null && !serverSocket.isClosed() ) {
                serverSocket.close();
                serverSocket = null;
            }
        }  catch(Exception e) {
            logger.WriteLine( e.getMessage(), getCurrentThreadName(), getClassName(), "StopListenning" );
        }
    }

    // -------- PRIVATE ---------------------

    private ServerSocket serverSocket = null;
    private AtomicBoolean shouldWaiting;
    private int portN;
    private Logger logger;
    private List<Sender> senders;
    private QueuesHolder queuesHolder;

    // Start listening for new connection from client
    // Every new connection is run in separate thread with class Sender
    // Method serverSocket.accept() is blocking, so we are waiting on this method till the client is connected
    private void waitForNewConnections() {
        if( serverSocket == null ) {
            logger.WriteLine( "Cannot listen to connections. ServerSocket is null", getCurrentThreadName(), getClassName(), "waitForNewConnection"  );
            return;
        }
        try{
            while(shouldWaiting.get()) {
                // Method serverSocket.accept() is blocking,
                // so we are waiting on this method till the client is connected
                Socket fromClient = serverSocket.accept();
                // Create the queue with data (from gyroscope and accelerometer sensors)
                // The client will receive the data from this queue
                GyroDataQueue newDataQueue = queuesHolder.AddNewQueue();
                if(newDataQueue == null) {
                    throw new Exception("GyroDataQueue newDataQueue = queuesHolder.AddNewQueue() returns null!");
                }
                Sender sender = new Sender(fromClient, logger, newDataQueue);
                senders.add(sender);
                logger.WriteLine("New Connection #" + senders.size() + " was detected");
                Thread newThread = new Thread(sender);
                newThread.setName("Connection #" + senders.size());
                newThread.start();
                // If connection with client was established we continue to wait for the next connection to the same port
            }
        } catch(IOException e) {
            if( e.getMessage().contains("socket closed")) {
                logger.WriteLine( "ServerSocket.accept() stops waiting for new connection", getCurrentThreadName(), getClassName(), "waitForNewConnection"  );
            } else {
                logger.WriteLine( "ConnectionListener IOException " + e.getMessage(), getCurrentThreadName(), getClassName(), "waitForNewConnection"  );
            }
        } catch(Exception e) {
            logger.WriteLine( e.getMessage(), getCurrentThreadName(), getClassName(), "waitForNewConnection" );
        }
    }
    private String getCurrentThreadName() {
        return Thread.currentThread().getName();
    }

    private String getClassName() {
        return this.getClass().getName();
    }
}
