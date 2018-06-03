package com.azzgil.coeditor.beans.services.documents;

import com.azzgil.coeditor.model.Document;
import com.azzgil.coeditor.model.DocumentVersion;
import com.azzgil.coeditor.utils.hibernate.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.NoResultException;
import java.time.LocalDateTime;
import java.util.List;

public class DocumentVersionServiceHibernateImpl implements DocumentVersionService {

    private static final String INITIAL_VERSION = "initial version";

    private SessionFactory sessionFactory;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
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
        try (Session session = sessionFactory.openSession()) {
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

    @Override
    public DocumentVersion getLabelledVersionOf(int documentId, String versionLabel) throws Exception {

        // if there are several same-labelled versions of document
        // select the most actual one
        // ? NON-DOCUMENTED BEHAVIOR ?
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from DocumentVersion where " +
                    "primaryKey.documentId = :id and versionLabel = :version " +
                    "order by primaryKey.modificationTime desc", DocumentVersion.class)
                    .setParameter("id", documentId)
                    .setParameter("version", versionLabel)
                    .setMaxResults(1)
                    .getSingleResult();
        }
    }

    @Override
    public boolean updateDocumentVersion(DocumentVersion documentVersion) throws Exception {
        return HibernateUtils.saveOrUpdateObject(sessionFactory, documentVersion);
    }

    @Override
    public List<String> getAllVersionLabelsOf(int documentId) throws Exception {

        // skip empty versions
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("select versionLabel from DocumentVersion where " +
                    "primaryKey.documentId = :id and length(versionLabel) > 0 order by primaryKey.modificationTime desc", String.class)
                    .setParameter("id", documentId)
                    .list();
        }
    }
}
