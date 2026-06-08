package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.Optional;

import static ru.practicum.shareit.user.UserMapper.toUser;
import static ru.practicum.shareit.user.UserMapper.toUserDto;

@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public Collection<UserDto> getAll() {
        return userStorage.getAll().stream().map(UserMapper::toUserDto).toList();
    }

    @Override
    public UserDto getUserById(long id) {
        Optional<User> user = userStorage.getUserById(id);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        return toUserDto(user.get());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userStorage.create(toUser(userDto));
        return toUserDto(user);
    }

    @Override
    public UserDto updateUser(long id, UserDto userDto) {
        Optional<User> user = userStorage.getUserById(id);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        User userUpdated = userStorage.update(id, toUser(userDto));
        return toUserDto(userUpdated);
    }

    @Override
    public void deleteUser(long id) {
        userStorage.deleteUser(id);
    }

}
