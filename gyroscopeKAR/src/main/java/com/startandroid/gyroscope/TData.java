package com.startandroid.gyroscope;

/**
 * Created by Acer-PC on 07.10.2015.
 */
public class TData {
    public TData() {
        x = y = z = 0;
        sensorName = "";
    }

    public TData( String _sensorName, String _time, double _x, double _y, double _z ) {
        x = _x;
        y = _y;
        z = _z;
        sensorName = _sensorName;
        time = _time;
    }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public String getSensorName() { return sensorName; }
    public String getTime() { return time; }

    // -------- PRIVATE ---------------------
    private String sensorName;
    private double x;
    private double y;
    private double z;
    private String time;
}
