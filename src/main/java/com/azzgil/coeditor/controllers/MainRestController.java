package com.azzgil.coeditor.controllers;

import com.azzgil.coeditor.beans.services.documents.DocumentService;
import com.azzgil.coeditor.beans.services.documents.DocumentVersionService;
import com.azzgil.coeditor.model.Document;
import com.azzgil.coeditor.model.DocumentVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.security.Principal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/rest")
public class MainRestController {

    private static final int INITIAL_CAPACITY = 16;
    private static final int PUSH_INTERVAL = 3000;
    private static final int FETCH_INTERVAL = 1000;
    private static final long ACTIVE_USERS_CHECK_DELAY = 2000;
    private static final long ACTIVE_USER_EXPIRE_TIME = PUSH_INTERVAL + FETCH_INTERVAL;
    private static final int COLLAPSE_SIZE = 5;

    private DocumentService documentService;
    private DocumentVersionService documentVersionService;
    private ScheduledExecutorService executorService;
    private ScheduledFuture<?> updateHandler;
    private HashMap<Integer, HashMap<String, Date>> activeUsers;

    @Autowired
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Autowired
    public void setDocumentVersionService(DocumentVersionService documentVersionService) {
        this.documentVersionService = documentVersionService;
    }

    @PostConstruct
    public void initialize() {
        activeUsers = new HashMap<>(INITIAL_CAPACITY);
        executorService = Executors.newScheduledThreadPool(1);
        updateHandler = executorService.scheduleWithFixedDelay(this::updateActiveUsers, 0,
                ACTIVE_USERS_CHECK_DELAY, TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void shutdown() {
        updateHandler.cancel(true);
    }

    private void updateActiveUsers() {
        for (Integer documentId : activeUsers.keySet()) {
            HashMap<String, Date> active = activeUsers.get(documentId);


            if (active != null) {
                Iterator<Entry<String, Date>> it = active.entrySet().iterator();
                while (it.hasNext()) {
                    Entry<String, Date> user = it.next();

                    if (new Date().getTime() - user.getValue().getTime()
                            >= ACTIVE_USER_EXPIRE_TIME) {
                        it.remove();
                    }
                }
            }
        }
    }

    private void registerActiveUser(Integer documentId, String username) {
        if (!activeUsers.containsKey(documentId)) {
            HashMap<String, Date> active = new HashMap<>();
            active.put(username, new Date());
            activeUsers.put(documentId, active);
        } else {
            activeUsers.get(documentId).put(username, new Date());
        }
    }

    @GetMapping("/docs")
    public List<Document> getAllAvailableDocuments() throws Exception {
        return documentService.getAllDocuments();
    }

    @PostMapping(path = "/docs/new", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void createDocument(@RequestBody Document document) throws Exception {
        documentService.createDocument(document);
    }

    @GetMapping("/docs/{id}")
    public DocumentVersion getLastDocumentVersion(@PathVariable(value = "id") Integer id, Principal principal)
            throws Exception {
        registerActiveUser(id, principal.getName());
        return documentVersionService.getLastVersionOf(id);
    }

    @PostMapping(path = "/docs/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateDocument(@RequestBody DocumentVersion documentVersion, Principal principal)
            throws Exception {
        registerActiveUser(documentVersion.getDocument().getId(), principal.getName());
        if (!documentVersionService.saveDocumentVersion(documentVersion)) {
            throw new RuntimeException("unpredictable error");
        }
    }

    @GetMapping("/docs/{id}/lastupdate")
    public LocalDateTime getLastVersionOfDocument(
            @PathVariable(value = "id") int id, Principal principal)
            throws Exception {
        registerActiveUser(id, principal.getName());
        return documentVersionService.getLastUpdateTimeOf(id);
    }

    @GetMapping("/docs/{id}/activeusers")
    public String getActiveUsers(@PathVariable(value = "id") int documentId) {
        if (activeUsers.containsKey(documentId)) {
            Set<String> active = activeUsers.get(documentId).keySet();
            Iterator<String> it = active.iterator();
            String result = "Active users: ";

            int current = 1;
            while (it.hasNext()) {

                result += it.next();

                if (current == COLLAPSE_SIZE) {
                    result += " and " + (active.size() - COLLAPSE_SIZE) + " more";
                    break;
                } else if (it.hasNext()) {
                    result += ", ";
                }

                current++;
            }

            return result;
        } else {
            return "document is not being viewed by anyone";
        }
    }

    @GetMapping("/push_interval")
    public int getPushInterval() {
        return PUSH_INTERVAL;
    }

    @GetMapping("/fetch_interval")
    public int getFetchInterval() {
        return FETCH_INTERVAL;
    }
}
