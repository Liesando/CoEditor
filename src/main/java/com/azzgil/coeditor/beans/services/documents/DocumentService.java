package com.azzgil.coeditor.beans.services.documents;

import com.azzgil.coeditor.model.Document;

import java.sql.SQLException;

public interface DocumentService {

    /**
     * This method creates an entry in documents table along with
     * an entry in document versions table (i.e. calls the
     * {@link DocumentService#updateDocument(Document)} method)
     * to instantiate a brand new (empty) version of document.
     * @param document
     * @throws SQLException
     */
    boolean createDocument(Document document) throws SQLException;

    Document getDocumentById(int id) throws SQLException;

    /**
     * This method <i>does not</i> actually updates document, but
     * inserts a brand new version of it in the table.
     * @param document
     * @throws SQLException
     */
    boolean updateDocument(Document document) throws SQLException;

    boolean deleteDocument(Document document) throws SQLException;
}
