package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.expection.ConflictException;
import ru.practicum.shareit.expection.NotFoundException;
import ru.practicum.shareit.user.dao.UserStorageInMemory;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.Optional;

import static ru.practicum.shareit.user.UserMapper.toUser;
import static ru.practicum.shareit.user.UserMapper.toUserDto;

@Service
public class UserServiceImpl implements UserService {

    private final UserStorageInMemory userStorageInMemory;

    public UserServiceImpl(UserStorageInMemory userStorageInMemory) {
        this.userStorageInMemory = userStorageInMemory;
    }

    public Collection<UserDto> getAll() {
        return userStorageInMemory.getAll().stream().map(UserMapper::toUserDto).toList();
    }

    public UserDto getUserById(long id) {
        Optional<User> user = userStorageInMemory.getUserById(id);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        return toUserDto(user.get());
    }

    public UserDto createUser(UserDto userDto) {
        boolean emailExists = userStorageInMemory.isEmailExist(userDto.getEmail());
        if (emailExists) {
            throw new ConflictException("Email already exists");
        }
        User user = userStorageInMemory.create(toUser(userDto));
        return toUserDto(user);
    }

    public UserDto updateUser(long id, UserDto userDto) {
        boolean emailExists = userStorageInMemory.isEmailExist(userDto.getEmail());
        if (emailExists) {
            throw new ConflictException("Email already exists");
        }

        Optional<User> user = userStorageInMemory.getUserById(id);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }
        User userUpdated = userStorageInMemory.update(id, toUser(userDto));
        return toUserDto(userUpdated);
    }

    public void deleteUser(long id) {
        userStorageInMemory.deleteUser(id);
    }

}
