package com.azzgil.coeditor.utils.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class HibernateUtils {

    private HibernateUtils() {
    }

    public static boolean saveOrUpdateObject(SessionFactory sessionFactory, Object object) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.saveOrUpdate(object);
            session.getTransaction().commit();
            return true;
        } catch (HibernateException e) {
            session.getTransaction().rollback();
            return false;
        } finally {
            session.close();
        }
    }
}
