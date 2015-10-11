package com.startandroid.gyroscope;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.lang.Thread;

/**
 * Created by Acer-PC on 07.10.2015.
 */
public class ConnectionListener implements Runnable  {
    private Logger logger;
    private int port;
    Sender sender1;

    public ConnectionListener(int _port) {
        port = _port;
        logger = Logger.GetLogger();
    }

    public void run() {
        logger.WriteLine("Привет из потока ConnectionListener!");
        try{
            ServerSocket serverSocket = new ServerSocket(port);
            while(true) {
                Socket fromClient = serverSocket.accept();	// в отдельный поток - уже в отдельном потоке
                logger.WriteLine("New connection was detected");
                sender1 = new Sender(fromClient);
                Thread thr1 = new Thread(sender1);
                thr1.setName("Sender thread");
                thr1.start();
            }
        } catch(IOException e) {
            logger.WriteLine( "ConnectionListener IOException " + e.getMessage() );
        } catch(Exception e) {
            logger.WriteLine( e.getMessage() );
        }
    }

    public void StopAllConnections() {
        sender1.StopSending();
    }


}
