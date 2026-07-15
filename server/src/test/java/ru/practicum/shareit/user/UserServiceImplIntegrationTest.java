package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class UserServiceImplIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User saveUser(String name, String email) {
        return userRepository.save(User.builder().name(name).email(email).build());
    }

    @Test
    void createUser_savesUserToDatabase() {
        UserDto dto = UserDto.builder().name("Alice").email("alice@example.com").build();

        UserDto created = userService.createUser(dto);

        assertNotNull(created.getId());
        User saved = userRepository.findById(created.getId()).orElseThrow();
        assertEquals("Alice", saved.getName());
        assertEquals("alice@example.com", saved.getEmail());
    }

    @Test
    void createUser_duplicateEmail_throwsConflictException() {
        saveUser("Alice", "alice@example.com");
        UserDto dto = UserDto.builder().name("Alice Clone").email("ALICE@example.com").build();

        assertThrows(ConflictException.class, () -> userService.createUser(dto));
    }

    @Test
    void getAll_returnsAllUsers() {
        saveUser("Alice", "alice@example.com");
        saveUser("Bob", "bob@example.com");

        Collection<UserDto> result = userService.getAll();

        assertTrue(result.stream().anyMatch(u -> u.getEmail().equals("alice@example.com")));
        assertTrue(result.stream().anyMatch(u -> u.getEmail().equals("bob@example.com")));
    }

    @Test
    void getUserById_returnsUserFromDatabase() {
        User user = saveUser("Alice", "alice@example.com");

        UserDto result = userService.getUserById(user.getId());

        assertEquals(user.getId(), result.getId());
        assertEquals("Alice", result.getName());
        assertEquals("alice@example.com", result.getEmail());
    }

    @Test
    void getUserById_unknownId_throwsNotFoundException() {
        assertThrows(NotFoundException.class, () -> userService.getUserById(9999L));
    }

    @Test
    void updateUser_updatesOnlyProvidedFields() {
        User user = saveUser("Alice", "alice@example.com");
        UserUpdateDto patch = UserUpdateDto.builder().name("Alice Updated").build();

        UserDto result = userService.updateUser(user.getId(), patch);

        assertEquals("Alice Updated", result.getName());
        assertEquals("alice@example.com", result.getEmail());
        User saved = userRepository.findById(user.getId()).orElseThrow();
        assertEquals("Alice Updated", saved.getName());
        assertEquals("alice@example.com", saved.getEmail());
    }

    @Test
    void updateUser_existingEmail_throwsConflictException() {
        saveUser("Alice", "alice@example.com");
        User bob = saveUser("Bob", "bob@example.com");
        UserUpdateDto patch = UserUpdateDto.builder().email("alice@example.com").build();

        assertThrows(ConflictException.class, () -> userService.updateUser(bob.getId(), patch));
    }

    @Test
    void deleteUser_removesUserFromDatabase() {
        User user = saveUser("Alice", "alice@example.com");

        userService.deleteUser(user.getId());

        assertTrue(userRepository.findById(user.getId()).isEmpty());
    }

}
