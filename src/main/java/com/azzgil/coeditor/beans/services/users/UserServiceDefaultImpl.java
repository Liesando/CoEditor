package com.azzgil.coeditor.beans.services.users;

import com.azzgil.coeditor.beans.services.db.DBService;
import com.azzgil.coeditor.database.SqlQueryTemplates;
import com.azzgil.coeditor.database.dbmeta.DBColumns;
import com.azzgil.coeditor.model.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserServiceDefaultImpl implements UserService {

    private DBService dbService;

    @Autowired
    public void setDbService(DBService dbService) {
        this.dbService = dbService;
    }

    @Override
    public User getUserByUsername(String username) throws SQLException {
        try (PreparedStatement ps = dbService.getConnection()
                .prepareStatement(SqlQueryTemplates.SELECT_USER_BY_USERNAME_PREPARED)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? parseUser(rs) : null;
        }
    }

    private User parseUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUsername(rs.getString(DBColumns.USER_USERNAME));
        user.setPassword(rs.getString(DBColumns.USER_PASSWORD));
        return user;
    }

    @Override
    public boolean registerUser(User user) throws SQLException {

        // already exists
        if (getUserByUsername(user.getUsername()) != null) {
            return false;
        }

        try (PreparedStatement ps = dbService.getConnection()
                .prepareStatement(SqlQueryTemplates.INSERT_USER_PREPARED)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());

            boolean result = ps.executeUpdate() == 1; // <-------------- LOOK HERE, FRIEND
            return result;
        } catch (SQLException e) {
            return false;
        }
    }
}
