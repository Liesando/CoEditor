package com.azzgil.coeditor.model;

import javax.persistence.*;

@Entity
@Table(name = "DOCUMENTS")
public class Document {

    private int id;
    private String name;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
