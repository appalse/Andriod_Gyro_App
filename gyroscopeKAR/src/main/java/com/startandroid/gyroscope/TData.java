package com.startandroid.gyroscope;

// Class is intended only for extracting data. The data cannot be overwritten
// Therefore there is no need to use synchronization

public class TData {
    public TData() {
        x = y = z = 0;
        sensorId = 0;
        time = 0;
    }

    public TData( short _sensorId, long _time, double _x, double _y, double _z ) {
        x = _x;
        y = _y;
        z = _z;
        sensorId = _sensorId;
        time = _time;
    }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public short getSensorId() { return sensorId; }
    public long getTime() { return time; }

    // -------- PRIVATE ---------------------
    private short sensorId; // gyroscope = x1A9, accelerometer = 0x1AA
    private double x;
    private double y;
    private double z;
    private long time;
}
