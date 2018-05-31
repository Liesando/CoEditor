package com.azzgil.coeditor.model;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "DOCUMENTS")
public class Document {

    public static final String INITIAL_STATE_LABEL = "INITIAL STATE";

    private int id;
    private String name;
    private String data;
    private LocalDateTime lastModification;
    private String versionLabel = INITIAL_STATE_LABEL;

    @Id
    @Column(name = "DOCUMENT_ID")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "DOCUMENT_NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(table = "DOCUMENT_VERSIONS", name = "DOCUMENT_DATA")
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Transient
    public LocalDateTime getLastModification() {
        return lastModification;
    }

    public void setLastModification(LocalDateTime lastModification) {
        this.lastModification = lastModification;
    }

    @Column(table = "DOCUMENT_VERSIONS", name = "VERSION_NAME")
    public String getVersionLabel() {
        return versionLabel;
    }

    public void setVersionLabel(String versionLabel) {
        this.versionLabel = versionLabel;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
