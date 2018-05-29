package com.azzgil.coeditor.configs;

import com.azzgil.coeditor.beans.services.db.DBService;
import com.azzgil.coeditor.beans.services.db.DBServiceH2Impl;
import com.azzgil.coeditor.beans.services.documents.DocumentService;
import com.azzgil.coeditor.beans.services.documents.DocumentServiceDefaultImpl;
import com.azzgil.coeditor.beans.services.users.UserService;
import com.azzgil.coeditor.beans.services.users.UserServiceDefaultImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.ApplicationScope;

@Configuration
public class AppConfig {

    @Bean
    @ApplicationScope
    public DBService dbService() {
        return new DBServiceH2Impl();
    }

    @Bean
    @ApplicationScope
    public UserService userService() {
        return new UserServiceDefaultImpl();
    }

    @Bean
    @ApplicationScope
    public DocumentService documentService() {
        return new DocumentServiceDefaultImpl();
    }
}
