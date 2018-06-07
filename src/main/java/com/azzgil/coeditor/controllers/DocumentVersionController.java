package com.azzgil.coeditor.controllers;

import com.azzgil.coeditor.beans.services.documents.DocumentVersionService;
import com.azzgil.coeditor.beans.services.users.ActiveUsersService;
import com.azzgil.coeditor.model.DocumentVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/rest")
public class DocumentVersionController {

    @Autowired private DocumentVersionService documentVersionService;
    @Autowired private ActiveUsersService activeUsersService;

    @PutMapping(path = "/docs")
    public void updateDocument(@RequestBody DocumentVersion documentVersion, Principal principal)
            throws Exception {
        activeUsersService.registerActiveUser(documentVersion.getDocument().getId(), principal.getName());
        if (!documentVersionService.saveDocumentVersion(documentVersion)) {
            throw new RuntimeException("unpredictable error");
        }
    }

    @PatchMapping(path = "/docs")
    public void labelDocument(@RequestBody DocumentVersion documentVersion, Principal principal)
            throws Exception {
        activeUsersService.registerActiveUser(documentVersion.getPrimaryKey().getDocumentId(),
                principal.getName());

        // mapping '/rest/docs/1/version/all' is already taken
        if(documentVersion.getVersionLabel().trim().equals("all")) {
            return;
        }
        documentVersionService.updateDocumentVersion(documentVersion);
    }

    @GetMapping("/docs/{id}")
    public DocumentVersion getLastDocumentVersion(@PathVariable(value = "id") int id, Principal principal)
            throws Exception {
        activeUsersService.registerActiveUser(id, principal.getName());
        return documentVersionService.getLastVersionOf(id);
    }

    @GetMapping("/docs/{id}/version/{version}")
    public DocumentVersion getLabelledDocumentVersion(@PathVariable("id") int id,
                                                      @PathVariable("version") String versionLabel,
                                                      Principal principal) throws Exception {
        activeUsersService.registerActiveUser(id, principal.getName());
        return documentVersionService.getLabelledVersionOf(id, versionLabel);
    }

    @GetMapping("docs/{id}/version/all")
    public List<String> getAllVersionsOf(@PathVariable("id") int documentId, Principal principal)
            throws Exception {
        activeUsersService.registerActiveUser(documentId, principal.getName());
        return documentVersionService.getAllVersionLabelsOf(documentId);
    }

    @GetMapping("/docs/{id}/lastupdate")
    public LocalDateTime getLastUpdateTimeOfDocument(
            @PathVariable("id") int id, Principal principal)
            throws Exception {
        activeUsersService.registerActiveUser(id, principal.getName());
        return documentVersionService.getLastUpdateTimeOf(id);
    }
}
