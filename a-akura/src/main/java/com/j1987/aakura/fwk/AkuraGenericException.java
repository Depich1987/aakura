package com.j1987.aakura.fwk;

public class AkuraGenericException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7790616169617166724L;

	public AkuraGenericException() {
		
	}

	public AkuraGenericException(String message) {
		super(message);
	}

	public AkuraGenericException(Throwable cause) {
		super(cause);
	}

	public AkuraGenericException(String message, Throwable cause) {
		super(message, cause);
	}

}
