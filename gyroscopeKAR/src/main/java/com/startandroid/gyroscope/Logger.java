package com.startandroid.gyroscope;

import java.io.FileWriter;
import java.io.PrintWriter;

public class Logger implements ILogger {
    public Logger( String _filePath) {
        filePath = _filePath;
    }

    public void WriteLineInConsole( String text ) {
        System.out.println(text);
    }

    public void LogDebug( String className, String message ) {
        writeLineInConsole( "[DEBUG] in class " + className + " : " + message );
    }
    public void LogError( String className, String message ) {
        writeLineInConsole( "[ERROR] in class " + className + " : " + message );
    }

    private String filePath;

    private  void writeLineInConsole( String text ) {
        System.out.println( text );
    }

    private void writeLineInFile( String text ) {
        PrintWriter writer = null;
        try{
            writer = new PrintWriter(filePath);
            writer.println( text );
        } catch( Exception e ) {
            System.out.println( e.getMessage() );
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
