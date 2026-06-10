package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemStorage itemStorage;

    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).name("Alice").email("alice@example.com").build();
        item = Item.builder().id(1L).owner(user).name("Drill").description("Powerful drill").available(true).build();
        itemDto = ItemDto.builder().name("Drill").description("Powerful drill").available(true).build();
    }

    // --- getAll ---

    @Test
    void getAll_existingUser_returnsOwnItems() {
        when(userStorage.getUserById(1L)).thenReturn(Optional.of(user));
        when(itemStorage.getAll(1L)).thenReturn(List.of(item));

        Collection<ItemDto> result = itemService.getAll(1L);

        assertEquals(1, result.size());
        assertEquals("Drill", result.iterator().next().getName());
    }

    @Test
    void getAll_unknownUser_throwsNotFoundException() {
        when(userStorage.getUserById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getAll(99L));
        verify(itemStorage, never()).getAll(anyLong());
    }

    // --- getItemById ---

    @Test
    void getItemById_existingItem_returnsItemDto() {
        when(userStorage.getUserById(1L)).thenReturn(Optional.of(user));
        when(itemStorage.getItemById(1L)).thenReturn(Optional.of(item));

        ItemDto result = itemService.getItemById(1L, 1L);

        assertEquals("Drill", result.getName());
        assertTrue(result.getAvailable());
    }

    @Test
    void getItemById_unknownUser_throwsNotFoundException() {
        when(userStorage.getUserById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(1L, 99L));
        verify(itemStorage, never()).getItemById(anyLong());
    }

    @Test
    void getItemById_unknownItem_throwsNotFoundException() {
        when(userStorage.getUserById(1L)).thenReturn(Optional.of(user));
        when(itemStorage.getItemById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(99L, 1L));
    }

    // --- add ---

    @Test
    void add_validUserAndItem_createsAndReturnsItemDto() {
        when(userStorage.getUserById(1L)).thenReturn(Optional.of(user));
        when(itemStorage.create(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.add(itemDto, 1L);

        assertEquals("Drill", result.getName());
        verify(itemStorage).create(any(Item.class));
    }

    @Test
    void add_unknownUser_throwsNotFoundException() {
        when(userStorage.getUserById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.add(itemDto, 99L));
        verify(itemStorage, never()).create(any());
    }

    // --- update ---

    @Test
    void update_validOwner_updatesAndReturnsItemUpdateDto() {
        ItemUpdateDto patch = ItemUpdateDto.builder().name("Big Drill").build();
        Item updated = Item.builder().id(1L).owner(user).name("Big Drill").description("Powerful drill").available(true).build();

        when(userStorage.getUserById(1L)).thenReturn(Optional.of(user));
        when(itemStorage.update(eq(1L), eq(1L), any(Item.class))).thenReturn(updated);

        ItemUpdateDto result = itemService.update(1L, 1L, patch);

        assertEquals("Big Drill", result.getName());
    }

    @Test
    void update_unknownUser_throwsNotFoundException() {
        ItemUpdateDto patch = ItemUpdateDto.builder().name("Big Drill").build();
        when(userStorage.getUserById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.update(1L, 99L, patch));
        verify(itemStorage, never()).update(anyLong(), anyLong(), any());
    }

    // --- search ---

    @Test
    void search_validUser_returnsMatchingItems() {
        when(userStorage.getUserById(1L)).thenReturn(Optional.of(user));
        when(itemStorage.search("drill")).thenReturn(List.of(item));

        Collection<ItemDto> result = itemService.search("drill", 1L);

        assertEquals(1, result.size());
        assertEquals("Drill", result.iterator().next().getName());
    }

    @Test
    void search_emptyText_returnsEmptyList() {
        when(userStorage.getUserById(1L)).thenReturn(Optional.of(user));
        when(itemStorage.search("")).thenReturn(List.of());

        Collection<ItemDto> result = itemService.search("", 1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void search_unknownUser_throwsNotFoundException() {
        when(userStorage.getUserById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.search("drill", 99L));
        verify(itemStorage, never()).search(any());
    }
}
