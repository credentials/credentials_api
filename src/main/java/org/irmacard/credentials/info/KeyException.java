package org.irmacard.credentials.info;

/**
 * Exception class for private or public key-related errors.
 */
public class KeyException extends Exception {
	public KeyException() {
	}

	public KeyException(String message) {
		super(message);
	}

	public KeyException(String message, Throwable cause) {
		super(message, cause);
	}

	public KeyException(Throwable cause) {
		super(cause);
	}
}
