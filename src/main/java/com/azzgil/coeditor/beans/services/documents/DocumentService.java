package com.azzgil.coeditor.beans.services.documents;

import com.azzgil.coeditor.model.Document;

import java.sql.SQLException;
import java.util.List;

public interface DocumentService {

    /**
     * This method creates an entry in documents table along with
     * an entry in document versions table (i.e. calls the
     * {@link DocumentService#updateDocument(Document)} method)
     * to instantiate a brand new (empty) version of document.
     *
     * @param document
     * @throws SQLException
     */
    boolean createDocument(Document document) throws SQLException, DocumentVersionNotFoundException;

    Document getDocumentById(int id) throws SQLException, DocumentVersionNotFoundException;

    List<Document> getAllDocuments() throws SQLException;

    /**
     * This method <i>does not</i> actually updates document, but
     * inserts a brand new version of it in the table.
     *
     * @param document
     * @throws SQLException
     */
    boolean updateDocument(Document document) throws SQLException;

    /**
     * This method is not going to be used anywhere so
     * it does nothing by now.
     *
     * @param document
     * @return
     * @throws SQLException
     */
    boolean deleteDocument(Document document) throws SQLException;
}
