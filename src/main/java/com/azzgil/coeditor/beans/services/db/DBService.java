package com.azzgil.coeditor.beans.services.db;

import java.sql.Connection;
import java.sql.SQLException;

public interface DBService {
    void init() throws SQLException;
    void shutdown();
    Connection getConnection();
}
