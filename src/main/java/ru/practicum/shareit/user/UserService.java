package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.Collection;

public interface UserService {

    Collection<UserDto> getAll();

    UserDto getUserById(long id);

    UserDto createUser(UserDto userDto);

    UserDto updateUser(long id, UserUpdateDto userUpdateDto);

    void deleteUser(long id);

}
