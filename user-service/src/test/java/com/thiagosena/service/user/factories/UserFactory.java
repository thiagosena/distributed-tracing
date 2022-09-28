package com.thiagosena.service.user.factories;

import com.thiagosena.entities.User;

import java.util.List;

public class UserFactory {
    public static List<User> getUsers() {
        return List.of(
                new User(1L, "borabill"),
                new User(2L, "jonh")
        );
    }
}
