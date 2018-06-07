package com.azzgil.coeditor.controllers;

import com.azzgil.coeditor.beans.services.users.ActiveUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest")
public class ActiveUsersController {

    @Autowired private ActiveUsersService activeUsersService;

    @GetMapping("/docs/{id}/activeusers")
    public String getActiveUsers(@PathVariable(value = "id") int documentId) {
        return activeUsersService.getActiveUsersOf(documentId);
    }
}
