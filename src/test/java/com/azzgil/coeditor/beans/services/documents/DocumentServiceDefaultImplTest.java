package com.azzgil.coeditor.beans.services.documents;

import com.azzgil.coeditor.beans.services.db.DBService;
import com.azzgil.coeditor.beans.services.db.DBServiceH2Impl;
import com.azzgil.coeditor.model.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class DocumentServiceDefaultImplTest {

    private DBService dbService;
    private DocumentServiceDefaultImpl documentService = new DocumentServiceDefaultImpl();

    @Before
    public void setUp() throws Exception {
        dbService = new DBServiceH2Impl();
        dbService.init();
        documentService.setDbService(dbService);
    }

    @After
    public void tearDown() throws Exception {
        dbService.shutdown();
    }

    @Test
    public void createDocument() {
        Document document = new Document();
        document.setName("some super document");
        document.setData("not empty, but it's ok!");
        boolean actual = false;
        try {
            actual = documentService.createDocument(document);
        } catch (SQLException e) {
            actual = false;
        }

        assertTrue(actual);
    }

    @Test
    public void updateDocument() throws SQLException {
        Document document = new Document();
        document.setId(1);
        document.setName("some super document");
        document.setData("some new data");
        boolean actual = documentService.updateDocument(document);
        assertTrue(actual);
    }

    @Test
    public void getDocumentById() throws SQLException {
        Document document = documentService.getDocumentById(1);
        assertTrue(document.getName().equals("some super document"));
    }

    @Test
    public void deleteDocument() {
    }
}