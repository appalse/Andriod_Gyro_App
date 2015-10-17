package com.startandroid.gyroscope;

public class Logger {
	public Logger() {
	}

	public void WriteLine( String text ) {
		System.out.println( text );
	}

	public void WriteLine( String text, String className ) {
		System.out.println( "[" + className + "] : " + text  );
	}

	public void WriteLine( String text, String className, String methodName ) {
		System.out.println( "[" + className + "][" + methodName + "] : " + text  );
	}

	public void WriteLine( String text, String threadName, String className, String methodName ) {
		System.out.println( "[Thread " + threadName + "][" + className + "::" + methodName + "] : " + text  );
	}
}

