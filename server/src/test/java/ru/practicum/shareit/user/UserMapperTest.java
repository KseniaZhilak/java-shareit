package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserBookerDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserMapperTest {

    @Test
    void toUserDto_mapsAllFields() {
        User user = User.builder().id(1L).name("Alice").email("alice@example.com").build();

        UserDto result = UserMapper.toUserDto(user);

        assertEquals(1L, result.getId());
        assertEquals("Alice", result.getName());
        assertEquals("alice@example.com", result.getEmail());
    }

    @Test
    void toUser_fromUserDto_mapsNameAndEmail() {
        UserDto dto = UserDto.builder().id(1L).name("Alice").email("alice@example.com").build();

        User result = UserMapper.toUser(dto);

        assertNull(result.getId());
        assertEquals("Alice", result.getName());
        assertEquals("alice@example.com", result.getEmail());
    }

    @Test
    void toUser_fromUserUpdateDto_mapsNameAndEmail() {
        UserUpdateDto dto = UserUpdateDto.builder().name("Bob").email("bob@example.com").build();

        User result = UserMapper.toUser(dto);

        assertEquals("Bob", result.getName());
        assertEquals("bob@example.com", result.getEmail());
    }

    @Test
    void toUserBookerDto_mapsId() {
        User user = User.builder().id(7L).name("Alice").email("alice@example.com").build();

        UserBookerDto result = UserMapper.toUserBookerDto(user);

        assertEquals(7L, result.getId());
    }

}
