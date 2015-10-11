package com.startandroid.gyroscope;
import java.util.Random;
import java.lang.Thread;

/**
 * Created by Acer-PC on 07.10.2015.
 */
public class ServerMain implements Runnable {
    private Logger logger;
    private ConnectionListener connectionListener;
    public ServerMain() {
        logger = Logger.GetLogger();
    }

    public void run()
    {
        logger.WriteLine("START SERVER!");
        startServer();
    }

    public void Close() {
        connectionListener.StopAllConnections();
    }

    private void startServer() {
        logger.WriteLine("Start server");
        int port1 = 12346;
        try {
            connectionListener = new ConnectionListener(port1);
            Thread thrListener = new Thread(connectionListener);
            thrListener.setName("ConnectionListener thread");
            thrListener.start();
            // данные с гироскопа обновляеются каждую милисекунду и отправляются в Sender, который в другом потоке
        } catch( Exception e ) {
            logger.WriteLine(e.getMessage());
        }
    }
}
