package org.jpaste.pastebin.exceptions;

public class ParseException extends Exception {
	private static final long serialVersionUID = -4230960075582953775L;

	public ParseException() {
	}

	public ParseException(String message) {
		super(message);
	}
}

