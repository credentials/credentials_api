package org.irmacard.credentials.info;

/**
 * An unchecked exception for when the description store or keystore has not been initialized.
 */
public class StoreException extends RuntimeException {
	public StoreException() {
	}

	public StoreException(String message) {
		super(message);
	}

	public StoreException(String message, Throwable cause) {
		super(message, cause);
	}

	public StoreException(Throwable cause) {
		super(cause);
	}
}
