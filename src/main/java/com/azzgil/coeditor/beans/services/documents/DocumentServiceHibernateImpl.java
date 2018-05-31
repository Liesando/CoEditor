package com.azzgil.coeditor.beans.services.documents;

import com.azzgil.coeditor.model.Document;
import com.azzgil.coeditor.utils.hibernate.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class DocumentServiceHibernateImpl implements DocumentService {

    private SessionFactory sessionFactory;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public boolean createDocument(Document document) throws SQLException, DocumentVersionNotFoundException {
        Session session = null;

        return HibernateUtils.saveOrUpdateObject(sessionFactory, document);
    }

    @Override
    public Document getDocumentById(int id) throws SQLException, DocumentVersionNotFoundException {
        try(Session session = sessionFactory.openSession()) {
            return session.createQuery("from Document where id = :id", Document.class)
                    .setParameter("id", id).getSingleResult();
        }
    }

    @Override
    public List<Document> getAllDocuments() throws SQLException {
        try(Session session = sessionFactory.openSession()) {
            List<Document> docs = session.createQuery("from Document").list();
            for (Document d: docs) {
                d.setLastModification(getLastUpdateOf(session, d.getId()));
            }
        }
    }

    private LocalDateTime getLastUpdateOf(Session session, int id) {
        return (LocalDateTime)session.createSQLQuery("select MAX(MODIFICATION_TIME) " +
                "FROM DOCUMENT_VERSIONS WHERE DOCUMENT_ID = " + id)
                .getSingleResult();
    }

    @Override
    public boolean updateDocument(Document document) throws SQLException {
        document.setLastModification(LocalDateTime.now());

        return HibernateUtils.saveOrUpdateObject(sessionFactory, document);
    }

    @Override
    public boolean deleteDocument(Document document) throws SQLException {
        return false;
    }
}
