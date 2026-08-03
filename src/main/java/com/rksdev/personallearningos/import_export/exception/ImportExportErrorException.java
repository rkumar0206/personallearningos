package com.rksdev.personallearningos.import_export.exception;

public class ImportExportErrorException extends RuntimeException {

    public ImportExportErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImportExportErrorException(String message) {
        super(message);
    }
}
