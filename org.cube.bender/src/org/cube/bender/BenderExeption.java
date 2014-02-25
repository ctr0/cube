package org.cube.bender;

public class BenderExeption extends Exception {
	
	public BenderExeption(String message) {
        super(message);
    }
	
	public BenderExeption(String message, Throwable cause) {
        super(message, cause);
    }

}
