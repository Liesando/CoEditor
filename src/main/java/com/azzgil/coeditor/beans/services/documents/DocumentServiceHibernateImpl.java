package com.azzgil.coeditor.beans.services.documents;

import com.azzgil.coeditor.model.Document;
import com.azzgil.coeditor.utils.hibernate.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DocumentServiceHibernateImpl implements DocumentService {

    private SessionFactory sessionFactory;
    private DocumentVersionService documentVersionService;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Autowired
    public void setDocumentVersionService(DocumentVersionService documentVersionService) {
        this.documentVersionService = documentVersionService;
    }

    @Override
    public boolean createDocument(Document document) throws Exception {
        boolean result = HibernateUtils.saveOrUpdateObject(sessionFactory, document);
        if (result) {
            result = documentVersionService.createDocumentVersion(document);
        }
        return result;
    }

    @Override
    public List<Document> getAllDocuments() throws Exception {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Document").list();
        }
    }
}
