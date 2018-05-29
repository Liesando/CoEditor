package com.azzgil.coeditor.beans.services.users;

import com.azzgil.coeditor.model.User;

import java.sql.SQLException;

public interface UserService {
    User getUserByUsername(String username) throws SQLException;

    boolean registerUser(User user) throws SQLException;
}
