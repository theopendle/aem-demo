package com.theopendle.core.genericentities.datasource.impl;

public class DataSourceException extends Exception {
    public DataSourceException(final String message) {
        super(message);
    }

    public DataSourceException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
