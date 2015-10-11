package com.startandroid.gyroscope;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;


/**
 * Created by Acer-PC on 07.10.2015.
 */
public class Sender  implements Runnable  {
    public static Logger logger;
    private Socket socket;
    private volatile boolean isRunning = true;
    public Sender(Socket _socket) {
        socket = _socket;
        logger = Logger.GetLogger();
    }

    public void run()
    {
        logger.WriteLine("Привет из потока Sender!");
        send();
    }

    public void start()   {
        logger.WriteLine("start Sender!");
    }

    public void StopSending() {
        logger.WriteLine("stop Sender!");
        isRunning = false;
    }

    private void send() {
        try {
            GyroQueue gyroQueue = GyroQueue.GetGyroQueue();
            logger.WriteLine("Server is started");
            int i = 0;
            TData data;
            String text;
            while(isRunning) {
                OutputStream os = socket.getOutputStream();
                data = gyroQueue.Poll();
                if( data != null ) {
                    text = data.getName() + " " + data.getTime() + " : " + data.getX() + ", " + data.getY() + ", " + data.getZ() + ".";
                    os.write(text.getBytes());
                    logger.WriteLine("Poll " + data.getX() + ", " + data.getY() + ", " + data.getZ());
                    logger.WriteLine("Text was sent!");
                }
            }
        } catch(IOException e) {
            logger.WriteLine( "Sender IOException" + e.getMessage() );
        } catch(Exception e) {
            logger.WriteLine( e.getMessage() );
        }
    }
}
