package com.startandroid.gyroscope;

import java.io.FileWriter;
import java.io.PrintWriter;

public class Logger implements ILogger {
    public Logger( String _filePath) {
        filePath = _filePath;
    }

/*    public void WriteLineInConsole( String text ) {
        System.out.println(text);
    }
*/
    public void LogDebug( String className, String message ) {
        String text = "[DEBUG] in class " + className + " : " + message;
        writeLineInConsole( text );
    }
    public void LogError( String className, String message ) {
        String text = "[ERROR] in class " + className + " : " + message;
        writeLineInConsole( text );
    }

    private String filePath;

    private synchronized void writeLineInConsole( String text ) {
        System.out.println( text );
    }

    private synchronized void writeLineInFile( String text ) {

    }
}
