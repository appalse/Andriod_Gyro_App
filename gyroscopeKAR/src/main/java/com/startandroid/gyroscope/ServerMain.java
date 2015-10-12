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

        logger = new Logger( "log.txt" );
    }

    public void run()
    {
        logger.LogDebug(this.getClass().getName().toString(), "START SERVER!");
        startServer();
    }

    public void Close() {
        connectionListener.StopAllConnections();
    }

    private void startServer() {
        logger.LogDebug( this.getClass().getName().toString(), "Start server");
        int port1 = 12346;
        try {
            connectionListener = new ConnectionListener(port1, logger);
            Thread thrListener = new Thread(connectionListener);
            logger.LogDebug(this.getClass().getName().toString(), "ConnectionListener thread");
            thrListener.start();
            // данные с гироскопа обновляеются каждую милисекунду и отправляются в Sender, который в другом потоке
        } catch( Exception e ) {
            logger.LogError(this.getClass().getName().toString(), e.getMessage() );
        }
    }
}
