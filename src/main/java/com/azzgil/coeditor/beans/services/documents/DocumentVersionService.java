package com.azzgil.coeditor.beans.services.documents;

import com.azzgil.coeditor.model.Document;
import com.azzgil.coeditor.model.DocumentVersion;

import java.time.LocalDateTime;
import java.util.List;

public interface DocumentVersionService {
    DocumentVersion getLastVersionOf(int documentId) throws Exception;

    DocumentVersion getLabelledVersionOf(int documentId, String versionLabel) throws Exception;

    LocalDateTime getLastUpdateTimeOf(int documentId) throws Exception;

    boolean createDocumentVersion(Document document) throws Exception;

    boolean saveDocumentVersion(DocumentVersion documentVersion) throws Exception;

    boolean updateDocumentVersion(DocumentVersion documentVersion) throws Exception;

    List<String> getAllVersionLabelsOf(int documentId) throws Exception;
}
