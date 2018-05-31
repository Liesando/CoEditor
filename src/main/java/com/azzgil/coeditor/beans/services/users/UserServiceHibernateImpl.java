package com.azzgil.coeditor.beans.services.users;

import com.azzgil.coeditor.model.User;
import com.azzgil.coeditor.utils.hibernate.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.NoResultException;
import java.sql.SQLException;

public class UserServiceHibernateImpl implements UserService {

    private SessionFactory sessionFactory;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public User getUserByUsername(String username) throws SQLException {
        try (Session session = sessionFactory.openSession()) {
            User user = session.createQuery("from User where username = :name", User.class)
                    .setParameter("name", username).getSingleResult();
            return user;
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public boolean registerUser(User user) throws SQLException {
        if (getUserByUsername(user.getUsername()) != null) {
            return false;
        }

        return HibernateUtils.saveOrUpdateObject(sessionFactory, user);
    }


}
