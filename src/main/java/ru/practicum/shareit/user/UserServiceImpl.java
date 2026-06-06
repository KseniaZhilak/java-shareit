package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserStorageInMemory;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

import static ru.practicum.shareit.user.UserMapper.toUser;
import static ru.practicum.shareit.user.UserMapper.toUserDto;

@Service
public class UserServiceImpl implements UserService {

    private final UserStorageInMemory userStorageInMemory;

    public UserServiceImpl(UserStorageInMemory userStorageInMemory) {
        this.userStorageInMemory = userStorageInMemory;
    }

    public Collection<UserDto> getAll() {
        return null;
    }

    public UserDto getUserById(long id) {
        return null;
    }

    public UserDto createUser(UserDto userDto) {
        boolean emailExists = userStorageInMemory.isEmailExist(toUser(userDto));
        if (emailExists) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = userStorageInMemory.create(toUser(userDto));

        return toUserDto(user);
    }

    public UserDto updateUser(long id, UserDto userDto) {
        return null;
    }

    public void deleteUser(long id) {

    }

}
