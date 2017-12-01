package org.irmacard.credentials.info.updater;

import java.lang.Exception;

class VerificationFailedException extends ConfigurationUpdateException {
    public VerificationFailedException() {}
    public VerificationFailedException(String message) {
        super(message);
    }
}

