package com.azzgil.coeditor.beans.services.documents;

import com.azzgil.coeditor.model.Document;
import com.azzgil.coeditor.model.DocumentVersion;

import java.util.List;

public interface DocumentService {

    /**
     * This method creates an entry in documents table along with
     * an entry in document versions table (i.e. calls the
     * {@link DocumentVersionService#saveDocumentVersion(DocumentVersion)} method)
     * to instantiate a brand new (empty) version of document.
     *
     * @param document
     * @throws Exception
     */
    boolean createDocument(Document document) throws Exception;

    List<Document> getAllDocuments() throws Exception;
}
