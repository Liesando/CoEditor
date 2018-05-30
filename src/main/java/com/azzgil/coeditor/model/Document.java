package com.azzgil.coeditor.model;

import java.time.LocalDateTime;

public class Document {

    public static final String INITIAL_STATE_LABEL = "INITIAL STATE";

    private int id;
    private String name;
    private String data;
    private LocalDateTime lastModification;
    private String versionLabel = INITIAL_STATE_LABEL;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public static String getInitialStateLabel() {
        return INITIAL_STATE_LABEL;
    }

    public LocalDateTime getLastModification() {
        return lastModification;
    }

    public void setLastModification(LocalDateTime lastModification) {
        this.lastModification = lastModification;
    }

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
