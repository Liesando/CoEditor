package com.azzgil.coeditor.beans.services.documents;

import com.azzgil.coeditor.model.Document;
import com.azzgil.coeditor.model.DocumentVersion;
import com.azzgil.coeditor.utils.hibernate.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.NoResultException;
import java.time.LocalDateTime;

public class DocumentVersionServiceHibernateImpl implements DocumentVersionService {

    private static final String INITIAL_VERSION = "initial version";

    private SessionFactory sessionFactory;
    private DocumentService documentService;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Autowired
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    @Override
    public DocumentVersion getLastVersionOf(int documentId) throws Exception {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                    "from DocumentVersion where primaryKey.documentId = :id " +
                            "order by primaryKey.modificationTime desc", DocumentVersion.class)
                    .setParameter("id", documentId)
                    .setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public LocalDateTime getLastUpdateTimeOf(int documentId) throws Exception {
        try(Session session = sessionFactory.openSession()) {
            return getLastVersionOf(documentId).getPrimaryKey().getModificationTime();
        }
    }

    @Override
    public boolean createDocumentVersion(Document document) throws Exception {
        DocumentVersion documentVersion = new DocumentVersion(document, "", INITIAL_VERSION);

        return HibernateUtils.saveOrUpdateObject(sessionFactory, documentVersion);
    }

    @Override
    public boolean saveDocumentVersion(DocumentVersion documentVersion) throws Exception {
        documentVersion.getPrimaryKey().setModificationTime(LocalDateTime.now());
        return HibernateUtils.saveOrUpdateObject(sessionFactory, documentVersion);
    }
}