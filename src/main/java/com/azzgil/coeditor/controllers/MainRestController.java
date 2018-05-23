package com.azzgil.coeditor.controllers;

import com.azzgil.coeditor.beans.services.documents.DocumentService;
import com.azzgil.coeditor.beans.services.documents.DocumentVersionNotFoundException;
import com.azzgil.coeditor.beans.services.documents.UpdateDocumentUnexpectedException;
import com.azzgil.coeditor.model.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rest")
public class MainRestController {

    private DocumentService documentService;

    @Autowired
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/docs")
    public List<Document> getAllAvailableDocuments() throws SQLException {
        return documentService.getAllDocuments();
    }

    @PostMapping(path = "/docs/new", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createDocument(@RequestBody Document document)
            throws SQLException, DocumentVersionNotFoundException {
        documentService.createDocument(document);
    }

    @GetMapping("/docs/{id}")
    public Document getLastDocumentVersion(@PathVariable(value = "id") Integer id)
            throws SQLException, DocumentVersionNotFoundException {
        return documentService.getDocumentById(id.intValue());
    }

    @PostMapping(path = "/docs/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateDocument(@RequestBody Document document)
            throws SQLException, UpdateDocumentUnexpectedException {
        if (!documentService.updateDocument(document)) {
            throw new UpdateDocumentUnexpectedException();
        }
    }

    @GetMapping("/docs/{id}/lastupdate")
    public LocalDateTime getLastVersionOfDocument(
            @PathVariable(value = "id", required = true) int id)
            throws SQLException, DocumentVersionNotFoundException {
        Document document = documentService.getDocumentById(id);
        return document.getLastModification();
    }
}
