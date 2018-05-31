package com.azzgil.coeditor.configs;

import com.azzgil.coeditor.beans.services.db.DBService;
import com.azzgil.coeditor.beans.services.db.DBServiceH2Impl;
import com.azzgil.coeditor.beans.services.documents.DocumentService;
import com.azzgil.coeditor.beans.services.documents.DocumentServiceHibernateImpl;
import com.azzgil.coeditor.beans.services.documents.DocumentVersionService;
import com.azzgil.coeditor.beans.services.documents.DocumentVersionServiceHibernateImpl;
import com.azzgil.coeditor.beans.services.users.UserService;
import com.azzgil.coeditor.beans.services.users.UserServiceHibernateImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.ApplicationScope;

@Configuration
public class AppConfig {

    @Bean
    @ApplicationScope
    public UserService userService() {
        return new UserServiceHibernateImpl();
    }

    @Bean
    @ApplicationScope
    public DocumentService documentService() {
        return new DocumentServiceHibernateImpl();
    }

    @Bean
    @ApplicationScope
    public DocumentVersionService documentVersionService() {
        return new DocumentVersionServiceHibernateImpl();
    }
}
