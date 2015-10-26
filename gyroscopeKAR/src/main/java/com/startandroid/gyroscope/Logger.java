package com.startandroid.gyroscope;

import android.os.Environment;
import java.io.File;
import java.io.*;

public class Logger {
	public Logger() {
		if(iStorageWriteable()) {
			file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "gyro.txt");
		} else {
			file = null;
		}
	}

	public void WriteLine( String text ) {
		write( text );
	}

	public void WriteLine( String text, String className ) {
		write("[" + className + "] : " + text);
	}

	public void WriteLine( String text, String className, String methodName ) {
		write("[" + className + "][" + methodName + "] : " + text);
	}

	public void WriteLine( String text, String threadName, String className, String methodName ) {
		write("[Thread " + threadName + "][" + className + "::" + methodName + "] : " + text);
	}

	private File file = null;
	private synchronized void write( String text ) {
		System.out.println( text );
		try {
			if (file != null) {
				FileOutputStream out = new FileOutputStream(file.getPath());
				out.write(text.getBytes());
				out.close();
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	private boolean iStorageWriteable() {
		String state = Environment.getExternalStorageState();
		if( Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}
}

