package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserRepository {

    private final Map<String, User> fakeDb = new HashMap<>();

    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(fakeDb.get(username));
    }

    public void save(User user) {
        fakeDb.put(user.getUsername(), user);
    }
}

