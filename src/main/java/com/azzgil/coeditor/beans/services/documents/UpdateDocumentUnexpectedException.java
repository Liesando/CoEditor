package com.azzgil.coeditor.beans.services.documents;

public class UpdateDocumentUnexpectedException extends Exception {

    public UpdateDocumentUnexpectedException() {
        super("INTERNAL ERROR: unexpected operation result during document update.");
    }
}
