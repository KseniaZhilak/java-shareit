package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserStorageInMemory {

    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> getAll() {
        return users.values();
    }

    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    public User create(User user) {
        Long id = getNextId();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    public User update(long id, User user) {
        User userUpdated = users.get(id);

        if(user.getName() != null) {
            userUpdated.setName(user.getName());
        }
        if(user.getEmail() != null) {
            userUpdated.setEmail(user.getEmail());
        }

        return userUpdated;
    }

    public void deleteUser(long id) {
        users.remove(id);
    }

    public Boolean isEmailExist(String email) {
        return users.values().stream()
                .anyMatch(e -> e.getEmail().equalsIgnoreCase(email));
    }

    private Long getNextId() {
        return users.size() + 1L;
    }

}
