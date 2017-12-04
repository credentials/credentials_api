package org.irmacard.credentials.info.updater;

import java.lang.Exception;

class IndexParsingException extends ConfigurationUpdateException {
    public IndexParsingException() {}
    public IndexParsingException(String message) {
        super(message);
    }
}

