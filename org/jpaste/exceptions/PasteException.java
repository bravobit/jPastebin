package org.jpaste.exceptions;

public class PasteException extends Exception {
	private static final long serialVersionUID = -4230960075582953775L;

	public PasteException() {
	}

	public PasteException(String message) {
		super(message);
	}
}

