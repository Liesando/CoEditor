package com.azzgil.coeditor.beans.authentication.users;

import com.azzgil.coeditor.beans.services.users.UserService;
import com.azzgil.coeditor.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.sql.SQLException;

public class UserDetailsServiceDefaultImpl implements UserDetailsService {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = userService.getUserByUsername(username);
            if(user == null) {
                throw new UsernameNotFoundException(username);
            }

            return new UserPrincipal(user);
        } catch (SQLException e) {
            throw new UsernameNotFoundException(username);
        }
    }
}
