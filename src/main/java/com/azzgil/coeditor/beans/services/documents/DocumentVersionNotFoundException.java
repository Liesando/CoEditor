package com.azzgil.coeditor.beans.services.documents;

public class DocumentVersionNotFoundException extends Exception {

    public DocumentVersionNotFoundException() {
        super("INTERNAL ERROR: specified document was not found in the database.");
    }
}
