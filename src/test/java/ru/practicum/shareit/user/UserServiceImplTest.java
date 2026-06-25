package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).name("Alice").email("alice@example.com").build();
        userDto = UserDto.builder().name("Alice").email("alice@example.com").build();
    }

    @Test
    void getAll_returnsAllUsers() {
        when(userStorage.getAll()).thenReturn(List.of(user));

        Collection<UserDto> result = userService.getAll();

        assertEquals(1, result.size());
        assertEquals("Alice", result.iterator().next().getName());
    }

    @Test
    void getAll_emptyStorage_returnsEmptyList() {
        when(userStorage.getAll()).thenReturn(List.of());

        Collection<UserDto> result = userService.getAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void getUserById_existingUser_returnsUserDto() {
        when(userStorage.getUserById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(1L);

        assertEquals("Alice", result.getName());
        assertEquals("alice@example.com", result.getEmail());
    }

    @Test
    void getUserById_unknownId_throwsNotFoundException() {
        when(userStorage.getUserById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    void createUser_savesAndReturnsUser() {
        when(userStorage.create(any(User.class))).thenReturn(user);

        UserDto result = userService.createUser(userDto);

        assertEquals("Alice", result.getName());
        verify(userStorage).create(any(User.class));
    }

    @Test
    void updateUser_existingUser_updatesAndReturnsUserDto() {
        UserUpdateDto patch = UserUpdateDto.builder().name("Bob").build();
        User updated = User.builder().id(1L).name("Bob").email("alice@example.com").build();

        when(userStorage.getUserById(1L)).thenReturn(Optional.of(user));
        when(userStorage.update(eq(1L), any(User.class))).thenReturn(updated);

        UserDto result = userService.updateUser(1L, patch);

        assertEquals("Bob", result.getName());
    }

    @Test
    void updateUser_unknownId_throwsNotFoundException() {
        UserUpdateDto patch = UserUpdateDto.builder().name("Bob").build();
        when(userStorage.getUserById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(99L, patch));
        verify(userStorage, never()).update(anyLong(), any());
    }

    @Test
    void deleteUser_existingUser_callsStorage() {
        userService.deleteUser(1L);
        verify(userStorage).deleteUser(1L);
    }

}
