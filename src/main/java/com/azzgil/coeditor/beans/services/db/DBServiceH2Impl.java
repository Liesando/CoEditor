package com.azzgil.coeditor.beans.services.db;

import com.azzgil.coeditor.utils.logging.ColoredLogger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBServiceH2Impl implements DBService {

    private ColoredLogger logger = new ColoredLogger(DBServiceH2Impl.class.getCanonicalName());
    private Connection connection;

    @PostConstruct
    @Override
    public void init() throws SQLException {
        try {
            logger.info("started initialization of db");

            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection("jdbc:h2:~/coeditor");

            logger.info("initialized successfully");
        } catch (ClassNotFoundException e) {
            logger.error("There's no database driver. Did you forget to add one?");
        }
    }

    @PreDestroy
    @Override
    public void shutdown() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
    }
}
