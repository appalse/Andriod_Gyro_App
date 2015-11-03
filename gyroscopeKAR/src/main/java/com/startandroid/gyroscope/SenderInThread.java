package com.startandroid.gyroscope;

// This class is created in separated thread by Sender class and
// it is responsible for sending the data from specified GyroDataQueue to the connected client
// The variable 'boolean shouldSending' can be changed from other thread therefore it is synchronized

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

public class SenderInThread implements Runnable {
    public SenderInThread(Logger _logger, OutputStream _os, QueuesHolder _queuesHolder, Object _mutex) {
        logger = _logger;
        os = _os;
        mutex = _mutex;
        queuesHolder = _queuesHolder;
        // Create the queue with data (from gyroscope and accelerometer sensors)
        // The client will receive the data from this queue
        dataQueue = _queuesHolder.AddNewQueue(mutex);
        shouldSending = new AtomicBoolean(false);
    }
    public void run() {
        logger.WriteLine("Hello from Sender thread!");
        if(dataQueue != null) {
            shouldSending.set(true);
            startSending();
        } else {
            logger.WriteLine("queuesHolder.AddNewQueue() cannot add queue!", getCurrentThreadName(), getClassName(), "run");
        }
    }

    // Stop sending data to the client
    public void Stop() {
        shouldSending.set(false);
    }

    // -------- PRIVATE ---------------------

    private Logger logger;
    private OutputStream os;
    private AtomicBoolean shouldSending;
    private GyroDataQueue dataQueue;
    private QueuesHolder queuesHolder;
    private Object mutex;

    // Start sending gyroscope and accelerometer data from specified data queue to the client
    private void startSending() {
        try {
            logger.WriteLine("Sending is started!");
            TData data;

            while(shouldSending.get() ) {
                synchronized( mutex ) {
                    while(!dataQueue.HasData()) {
                        try{
                            mutex.wait();
                        } catch (InterruptedException e) {}
                    }
                }
                data = dataQueue.Poll();
                if( data != null ) {
                    byte[] buffer = makeBuffer(data);
                    os.write(buffer);
                    logger.WriteLine("Text was sent! dataQueue.Poll() : " + buffer.toString());
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
    private byte[] makeBuffer( TData data )
    {
        int bufLength = 36; // bytes count for package with data
        byte[] buffer = new byte[bufLength]; // allocate the memory for 36 bytes
        buffer[0] = (byte) bufLength; // write the buffer size into first position of the buffer
        putShortValuesInBuffer(data.getSensorId(), 2, buffer, 1, 2); // 2 bytes to be out in buffer from 1 to 2 positions. This is package/data id : 0x1A9 - for gyroscope, 0x1AA - for accelerometer
        putLongValuesInBuffer(data.getTime(), 8, buffer, 3, 10); // 8 bytes. time in nanoseconds
        putDoubleValuesInBuffer(data.getX(), 8, buffer, 11, 18);
        putDoubleValuesInBuffer(data.getY(), 8, buffer, 19, 26);
        putDoubleValuesInBuffer(data.getZ(), 8, buffer, 27, 34);
        byte controlSum = getCheckSum(buffer, bufLength - 1); // checksum : XOR of al bytes of the package with data
        buffer[35] = controlSum;
        return buffer;
    }

    private void putShortValuesInBuffer(  short data, int size, byte[] buffer, int startPositionInBuffer, int endPositionInBuffer )
    {
        byte[] tempBuffer = ByteBuffer.allocate(size).putShort(data).array(); // 2 байта. id пакета: 0x1A9 - гироскоп, 0x1AA - акселерометр
        // уложить в buffer в позиции от start до end включительно
        for( int i = startPositionInBuffer; i <= endPositionInBuffer; ++i ) {
            buffer[i] = tempBuffer[i-startPositionInBuffer];
        }
    }

    private void putLongValuesInBuffer(  long data, int size, byte[] buffer, int startPositionInBuffer, int endPositionInBuffer )
    {
        byte[] tempBuffer = ByteBuffer.allocate(size).putLong(data).array(); // 8 байт
        // уложить в buffer в позиции от start до end включительно
        for( int i = startPositionInBuffer; i <= endPositionInBuffer; ++i ) {
            buffer[i] = tempBuffer[i-startPositionInBuffer];
        }
    }

    private void putDoubleValuesInBuffer( double data, int size, byte[] buffer, int startPositionInBuffer, int endPositionInBuffer )
    {
        byte[] tempBuffer = ByteBuffer.allocate(size).putDouble(data).array(); // 8 байт
        // уложить в buffer в позиции от start до end включительно
        for( int i = startPositionInBuffer; i <= endPositionInBuffer; ++i ) {
            buffer[i] = tempBuffer[i-startPositionInBuffer];
        }
    }

    private byte getCheckSum( byte[] buffer, int length )
    {
        byte xor = buffer[0];
        for( int i = 1; i < length; ++i ) {
            xor ^= buffer[i];
        }
        return xor;
    }

    private String getCurrentThreadName() {
        return Thread.currentThread().getName();
    }

    private String getClassName() {
        return this.getClass().getName();
    }
}
