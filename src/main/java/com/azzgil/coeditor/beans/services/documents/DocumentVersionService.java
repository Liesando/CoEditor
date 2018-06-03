package com.azzgil.coeditor.beans.services.documents;

import com.azzgil.coeditor.model.Document;
import com.azzgil.coeditor.model.DocumentVersion;

import java.time.LocalDateTime;
import java.util.List;

public interface DocumentVersionService {
    DocumentVersion getLastVersionOf(int documentId) throws Exception;

    DocumentVersion getLabelledVersionOf(int documentId, String versionLabel) throws Exception;

    LocalDateTime getLastUpdateTimeOf(int documentId) throws Exception;

    /**
     * Creates a brand new version of document with empty data
     * and labelled with <i>"initial version"</i>-label
     */
    boolean createDocumentVersion(Document document) throws Exception;

    /**
     * Saves (registers) a new version of document dated with current time and date
     * @param documentVersion
     * @return
     * @throws Exception
     */
    boolean saveDocumentVersion(DocumentVersion documentVersion) throws Exception;

    /**
     * Updates existing (or creates new if there's no any) version of document.
     * Main usage is to edit version label of document (particularly, add one).
     * @param documentVersion version label
     * @return true if successful, false otherwise
     * @throws Exception
     */
    boolean updateDocumentVersion(DocumentVersion documentVersion) throws Exception;

    List<String> getAllVersionLabelsOf(int documentId) throws Exception;
}
