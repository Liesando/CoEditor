package com.azzgil.coeditor.controllers;

import com.azzgil.coeditor.beans.services.users.UserService;
import com.azzgil.coeditor.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.SQLException;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private UserService userService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) throws SQLException {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        boolean result = userService.registerUser(user);
        return new ResponseEntity<>("", result? HttpStatus.OK: HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/me")
    public String getUsername(Principal principal) {
        return principal.getName();
    }
}
