package com.startandroid.gyroscope;

import java.io.FileWriter;

/**
 * Created by Acer-PC on 07.10.2015.
 */
public class Logger  {
    // Синглтон
    static public Logger GetLogger() {
        if( pLogger == null ) {
            pLogger = new Logger();
        }
        return pLogger;
    }

    public void WriteLine( String text ) {
        pLogger.writeLine( text );
    }

    // -------- PRIVATE ---------------------
    private Logger() {
        //pLogger.filePath = "D:\\log.txt";
       /* try {
            pLogger.writer = new FileWriter(filePath, true);
        } catch( Exception e ) {
           // FIXME надо как-то отловить
        }*/
    }
    static private Logger pLogger = null;
    private void writeLine( String text ) {
        System.out.println( text );
   /*     try {
            pLogger.writer.write(text);
        } catch( Exception e ) {
            // FIXME надо как-то отловить
        }*/

    }
    private FileWriter writer;
    private String filePath;
}
