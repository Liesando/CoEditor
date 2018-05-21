package com.azzgil.coeditor.beans.services.db;

import java.sql.Connection;

public interface DBService {
    void init();
    void shutdown();
    Connection getConnection();
}
