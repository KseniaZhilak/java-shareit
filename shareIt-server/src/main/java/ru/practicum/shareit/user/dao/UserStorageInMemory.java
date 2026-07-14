package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserStorageInMemory implements UserStorage {

    private long nextId = 1;

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User create(User user) {
        boolean emailExists = isEmailExist(user.getEmail());
        if (emailExists) {
            throw new ConflictException("Email already exists");
        }
        Long id = getNextId();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public User update(long id, User user) {
        boolean emailExists = users.values().stream()
                .filter(u -> !u.getId().equals(id))
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail()));
        if (emailExists) {
            throw new ConflictException("Email already exists");
        }

        User userUpdated = users.get(id);

        if (user.getName() != null) {
            userUpdated.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userUpdated.setEmail(user.getEmail());
        }

        return userUpdated;
    }

    @Override
    public void deleteUser(long id) {
        users.remove(id);
    }

    private boolean isEmailExist(String email) {
        return users.values().stream()
                .anyMatch(e -> e.getEmail().equalsIgnoreCase(email));
    }

    private long getNextId() {
        return nextId++;
    }

}
