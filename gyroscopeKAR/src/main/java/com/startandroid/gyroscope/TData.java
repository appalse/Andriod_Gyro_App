package com.startandroid.gyroscope;

/**
 * Created by Acer-PC on 07.10.2015.
 */
public class TData {	public TData() {
    x = y = z = 0;
    name = "";
}
    public TData( String _name, String _time, double _x, double _y, double _z ) {
        x = _x;
        y = _y;
        z = _z;
        name = _name;
        time = _time;
    }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public String getName() { return name; }
    public String getTime() { return time; }

    private String name;
    private double x;
    private double y;
    private double z;
    private String time;
}
