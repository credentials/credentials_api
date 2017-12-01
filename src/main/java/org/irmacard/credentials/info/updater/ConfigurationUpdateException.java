package org.irmacard.credentials.info.updater;

import java.lang.Exception;

class ConfigurationUpdateException extends Exception {
    public ConfigurationUpdateException() {}
    public ConfigurationUpdateException(String message) {
        super(message);
    }
}

