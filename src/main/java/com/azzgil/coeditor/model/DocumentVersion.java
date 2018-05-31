package com.azzgil.coeditor.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "DOCUMENT_VERSIONS")
public class DocumentVersion {

    @Embeddable
    public static class IdTimePk implements Serializable {
        private int documentId;
        private LocalDateTime modificationTime;

        public IdTimePk() {
        }

        public IdTimePk(int documentId, LocalDateTime modificationTime) {
            this.documentId = documentId;
            this.modificationTime = modificationTime;
        }

        @Column(name = "DOCUMENT_ID")
        public int getDocumentId() {
            return documentId;
        }

        public void setDocumentId(int documentId) {
            this.documentId = documentId;
        }

        @Column(name = "MODIFICATION_TIME")
        public LocalDateTime getModificationTime() {
            return modificationTime;
        }

        public void setModificationTime(LocalDateTime modificationTime) {
            this.modificationTime = modificationTime;
        }

        @Override
        public int hashCode() {
            return Objects.hash(documentId, modificationTime);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (!(obj instanceof IdTimePk)) {
                return false;
            }

            IdTimePk pk = (IdTimePk) obj;
            return documentId == pk.documentId
                    && Objects.equals(modificationTime, pk.modificationTime);
        }
    }

    private IdTimePk primaryKey;
    private String data;
    private String versionLabel;
    private Document document;

    public DocumentVersion() {
    }

    public DocumentVersion(Document document, String data, String versionLabel) {
        primaryKey = new IdTimePk(document.getId(), LocalDateTime.now());
        this.data = data;
        this.versionLabel = versionLabel;
    }

    @EmbeddedId
    public IdTimePk getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(IdTimePk primaryKey) {
        this.primaryKey = primaryKey;
    }

    @Column(name = "DOCUMENT_DATA")
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Column(name = "VERSION_NAME")
    public String getVersionLabel() {
        return versionLabel;
    }

    public void setVersionLabel(String versionLabel) {
        this.versionLabel = versionLabel;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DOCUMENT_ID", insertable = false, updatable = false)
    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}
