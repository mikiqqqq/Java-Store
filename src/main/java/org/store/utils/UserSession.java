package org.store.utils;

import org.store.model.User;

public class UserSession {
    private static UserSession instance;
    private User user;

    private UserSession() {
        // private constructor to prevent instantiation
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
