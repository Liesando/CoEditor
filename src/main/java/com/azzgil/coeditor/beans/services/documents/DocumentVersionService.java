package com.azzgil.coeditor.beans.services.documents;

import com.azzgil.coeditor.model.Document;
import com.azzgil.coeditor.model.DocumentVersion;

import java.time.LocalDateTime;

public interface DocumentVersionService {
    DocumentVersion getLastVersionOf(int documentId) throws Exception;

    LocalDateTime getLastUpdateTimeOf(int documentId) throws Exception;

    boolean createDocumentVersion(Document document) throws Exception;

    boolean saveDocumentVersion(DocumentVersion documentVersion) throws Exception;
}
