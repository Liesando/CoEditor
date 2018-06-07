package com.azzgil.coeditor.beans.services.users;

public interface ActiveUsersService {
    void registerActiveUser(Integer documentId, String username);

    String getActiveUsersOf(Integer documentId);
}
