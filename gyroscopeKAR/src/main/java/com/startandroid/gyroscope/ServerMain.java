package com.startandroid.gyroscope;
import android.view.ViewDebug;

import java.util.Random;
import java.lang.Thread;

/**
 * Created by Acer-PC on 07.10.2015.
 */
public class ServerMain implements Runnable {

    public ServerMain( QueuesContainer _gyroDataQueuesContainer ) {
        gyroDataQueuesContainer = _gyroDataQueuesContainer;
        logger = new Logger( "D:\\ServerMainLog.txt" );
    }

    public void run()
    {
        logger.LogDebug(this.getClass().getName().toString(), "START SERVER!");
        startServer();
    }

    public void Close() {
        connectionListener.StopAllConnections();
    }

    // -------- PRIVATE ---------------------
    private Logger logger;
    private ConnectionListener connectionListener;
    private QueuesContainer gyroDataQueuesContainer;

    private void startServer() {
        int port = 12346;
        logger.LogDebug(this.getClass().getName().toString(), "Start server. Port " + Integer.toString( port ) );

        try {
            connectionListener = new ConnectionListener(port, logger, gyroDataQueuesContainer);
            Thread thrListener = new Thread(connectionListener);
            logger.LogDebug(this.getClass().getName().toString(), "ConnectionListener thread");
            thrListener.start();
            // данные с гироскопа обновляеются каждую милисекунду и отправляются в Sender, который в другом потоке
        } catch( Exception e ) {
            logger.LogError(this.getClass().getName().toString(), e.getMessage() );
        }
    }
}
