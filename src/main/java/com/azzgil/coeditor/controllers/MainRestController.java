package com.azzgil.coeditor.controllers;

import com.azzgil.coeditor.beans.services.documents.DocumentService;
import com.azzgil.coeditor.beans.services.documents.DocumentVersionService;
import com.azzgil.coeditor.model.Document;
import com.azzgil.coeditor.model.DocumentVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.security.Principal;
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

    @Value("${coeditor.rest.push_interval}")
    private int PUSH_INTERVAL;

    @Value("${coeditor.rest.fetch_interval}")
    private int FETCH_INTERVAL;

    @Value("${coeditor.rest.users_check_delay}")
    private long ACTIVE_USERS_CHECK_DELAY;

    @Value("${coeditor.rest.active_user_expire_time}")
    private long ACTIVE_USER_EXPIRE_TIME;

    @Value("${coeditor.rest.active_users_collapse_size}")
    private int COLLAPSE_SIZE = 5;

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
        long time = new Date().getTime();
        for (Integer documentId : activeUsers.keySet()) {
            HashMap<String, Date> active = activeUsers.get(documentId);

            if (active != null) {
                active.entrySet().removeIf(e -> time - e.getValue().getTime() >= ACTIVE_USER_EXPIRE_TIME);
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
    public DocumentVersion getLastDocumentVersion(@PathVariable(value = "id") int id, Principal principal)
            throws Exception {
        registerActiveUser(id, principal.getName());
        return documentVersionService.getLastVersionOf(id);
    }

    @GetMapping("/docs/{id}/version/{version}")
    public DocumentVersion getLabelledDocumentVersion(@PathVariable("id") int id,
                                                      @PathVariable("version") String versionLabel,
                                                      Principal principal) throws Exception {
        registerActiveUser(id, principal.getName());
        return documentVersionService.getLabelledVersionOf(id, versionLabel);
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

    @PutMapping(path = "/docs", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void labelDocument(@RequestBody DocumentVersion documentVersion, Principal principal)
            throws Exception {
        registerActiveUser(documentVersion.getPrimaryKey().getDocumentId(), principal.getName());
        documentVersionService.updateDocumentVersion(documentVersion);
    }

    @GetMapping("/docs/{id}/lastupdate")
    public LocalDateTime getLastVersionOfDocument(
            @PathVariable("id") int id, Principal principal)
            throws Exception {
        registerActiveUser(id, principal.getName());
        return documentVersionService.getLastUpdateTimeOf(id);
    }

    @GetMapping("docs/{id}/version/all")
    public List<String> getAllVersionsOf(@PathVariable("id") int documentId, Principal principal)
            throws Exception {
        registerActiveUser(documentId, principal.getName());
        return documentVersionService.getAllVersionLabelsOf(documentId);
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

                if (it.hasNext()) {
                    if (current == COLLAPSE_SIZE) {
                        result += " and " + (active.size() - COLLAPSE_SIZE) + " more";
                        break;
                    } else {
                        result += ", ";
                    }
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
