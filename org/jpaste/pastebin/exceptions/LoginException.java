package org.jpaste.pastebin.exceptions;

public class LoginException extends Exception {
	private static final long serialVersionUID = -4230960075582953775L;

	public LoginException() {
	}

	public LoginException(String message) {
		super(message);
	}
}
