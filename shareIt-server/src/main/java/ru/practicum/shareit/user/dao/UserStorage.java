package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Optional<User> getUserById(long id);

    Collection<User> getAll();

    User create(User user);

    User update(long id, User user);

    void deleteUser(long id);

}
