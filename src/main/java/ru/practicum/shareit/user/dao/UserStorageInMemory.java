package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserStorageInMemory {

    private final Map<Long, User> users = new HashMap<>();

    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    public User create(User user) {
        Long id = getNextId();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    public Boolean isEmailExist(User user) {
        return users.values().stream()
                .anyMatch(e -> e.getEmail().equalsIgnoreCase(user.getEmail()));
    }

    private Long getNextId() {
        return users.size() + 1L;
    }

}
