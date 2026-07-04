package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

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
        when(userRepository.findAll()).thenReturn(List.of(user));

        Collection<UserDto> result = userService.getAll();

        assertEquals(1, result.size());
        assertEquals("Alice", result.iterator().next().getName());
    }

    @Test
    void getAll_emptyStorage_returnsEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        Collection<UserDto> result = userService.getAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void getUserById_existingUser_returnsUserDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(1L);

        assertEquals("Alice", result.getName());
        assertEquals("alice@example.com", result.getEmail());
    }

    @Test
    void getUserById_unknownId_throwsNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    void createUser_savesAndReturnsUser() {
        when(userRepository.existsByEmailIgnoreCase(userDto.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.createUser(userDto);

        assertEquals("Alice", result.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_existingUser_updatesAndReturnsUserDto() {
        UserUpdateDto patch = UserUpdateDto.builder().name("Bob").build();
        User updated = User.builder().id(1L).name("Bob").email("alice@example.com").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updated);

        UserDto result = userService.updateUser(1L, patch);

        assertEquals("Bob", result.getName());
    }

    @Test
    void updateUser_unknownId_throwsNotFoundException() {
        UserUpdateDto patch = UserUpdateDto.builder().name("Bob").build();
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(99L, patch));
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_existingUser_callsStorage() {
        userService.deleteUser(1L);
        verify(userRepository).deleteById(1L);
    }

}
