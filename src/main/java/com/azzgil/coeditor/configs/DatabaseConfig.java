package com.azzgil.coeditor.configs;

import com.azzgil.coeditor.annotations.SingletonScope;
import org.h2.jdbcx.JdbcDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Bean
    @SingletonScope
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setPackagesToScan("com.azzgil.coeditor.model");
        return sessionFactory;
    }

    @Bean
    @SingletonScope
    public DataSource dataSource(@Value("${coeditor.db.url}") String url) {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL(url);
        return dataSource;
    }
}