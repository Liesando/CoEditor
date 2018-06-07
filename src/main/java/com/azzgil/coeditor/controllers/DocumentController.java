package com.azzgil.coeditor.controllers;

import com.azzgil.coeditor.beans.services.documents.DocumentService;
import com.azzgil.coeditor.model.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest")
public class DocumentController {

    @Autowired private DocumentService documentService;

    @GetMapping("/docs")
    public List<Document> getAllAvailableDocuments() throws Exception {
        return documentService.getAllDocuments();
    }

    @PostMapping(path = "/docs")
    public void createDocument(@RequestBody Document document) throws Exception {
        documentService.createDocument(document);
    }
}
