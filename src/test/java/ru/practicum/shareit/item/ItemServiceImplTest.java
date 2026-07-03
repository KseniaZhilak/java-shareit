package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemsDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

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
        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item));
        when(bookingRepository.findAllByItemIdIn(List.of(1L))).thenReturn(List.of());
        when(commentRepository.findAllByItemIdIn(List.of(1L))).thenReturn(List.of());

        Collection<ItemsDto> result = itemService.getAll(1L);

        assertEquals(1, result.size());
        assertEquals("Drill", result.iterator().next().getName());
        assertTrue(result.iterator().next().getComments().isEmpty());
    }

    @Test
    void getAll_itemsWithComments_populatesComments() {
        Comment comment = Comment.builder().id(1L).text("Great!").item(item).author(user)
                .created(LocalDateTime.now()).build();

        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item));
        when(bookingRepository.findAllByItemIdIn(List.of(1L))).thenReturn(List.of());
        when(commentRepository.findAllByItemIdIn(List.of(1L))).thenReturn(List.of(comment));

        Collection<ItemsDto> result = itemService.getAll(1L);

        List<CommentDto> comments = result.iterator().next().getComments();
        assertEquals(1, comments.size());
        assertEquals("Great!", comments.get(0).getText());
        assertEquals("Alice", comments.get(0).getAuthorName());
    }

    // --- getItemById ---

    @Test
    void getItemById_existingItem_returnsItemDtoWithComments() {
        Comment comment = Comment.builder().id(1L).text("Nice").item(item).author(user)
                .created(LocalDateTime.now()).build();

        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(1L)).thenReturn(List.of(comment));

        ItemDto result = itemService.getItemById(1L, 1L);

        assertEquals("Drill", result.getName());
        assertTrue(result.getAvailable());
        assertEquals(1, result.getComments().size());
        assertEquals("Nice", result.getComments().get(0).getText());
    }

    @Test
    void getItemById_populatesLastAndNextBookingForOwner() {
        LocalDateTime lastStart = LocalDateTime.now().minusDays(5);
        LocalDateTime nextStart = LocalDateTime.now().plusDays(5);
        Booking lastBooking = Booking.builder().id(1L).item(item).booker(user)
                .start(lastStart).end(lastStart.plusDays(1)).status(Status.APPROVED).build();
        Booking nextBooking = Booking.builder().id(2L).item(item).booker(user)
                .start(nextStart).end(nextStart.plusDays(1)).status(Status.APPROVED).build();

        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(1L)).thenReturn(List.of());
        when(bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(
                eq(1L), eq(Status.APPROVED), any(LocalDateTime.class))).thenReturn(Optional.of(lastBooking));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                eq(1L), eq(Status.APPROVED), any(LocalDateTime.class))).thenReturn(Optional.of(nextBooking));

        ItemDto result = itemService.getItemById(1L, 1L);

        assertEquals(lastStart, result.getLastBooking());
        assertEquals(nextStart, result.getNextBooking());
    }

    @Test
    void getItemById_populatesLastAndNextBookingForNonOwner() {
        LocalDateTime lastStart = LocalDateTime.now().minusDays(5);
        LocalDateTime nextStart = LocalDateTime.now().plusDays(5);
        Booking lastBooking = Booking.builder().id(1L).item(item).booker(user)
                .start(lastStart).end(lastStart.plusDays(1)).status(Status.APPROVED).build();
        Booking nextBooking = Booking.builder().id(2L).item(item).booker(user)
                .start(nextStart).end(nextStart.plusDays(1)).status(Status.APPROVED).build();

        when(userRepository.existsById(2L)).thenReturn(true);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(1L)).thenReturn(List.of());
        when(bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(
                eq(1L), eq(Status.APPROVED), any(LocalDateTime.class))).thenReturn(Optional.of(lastBooking));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                eq(1L), eq(Status.APPROVED), any(LocalDateTime.class))).thenReturn(Optional.of(nextBooking));

        ItemDto result = itemService.getItemById(1L, 2L);

        assertEquals("Drill", result.getName());
        assertEquals(lastStart, result.getLastBooking());
        assertEquals(nextStart, result.getNextBooking());
    }

    @Test
    void getItemById_unknownUser_throwsNotFoundException() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemService.getItemById(1L, 99L));
        verify(itemRepository, never()).findById(anyLong());
    }

    @Test
    void getItemById_unknownItem_throwsNotFoundException() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(99L, 1L));
    }

    // --- add ---

    @Test
    void add_validUserAndItem_createsAndReturnsItemDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.add(itemDto, 1L);

        assertEquals("Drill", result.getName());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void add_unknownUser_throwsNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.add(itemDto, 99L));
        verify(itemRepository, never()).save(any());
    }

    // --- update ---

    @Test
    void update_validOwner_updatesAndReturnsItemUpdateDto() {
        ItemUpdateDto patch = ItemUpdateDto.builder().name("Big Drill").build();
        Item updated = Item.builder().id(1L).owner(user).name("Big Drill").description("Powerful drill").available(true).build();

        when(itemRepository.findByIdAndOwnerId(1L, 1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(updated);

        ItemUpdateDto result = itemService.update(1L, 1L, patch);

        assertEquals("Big Drill", result.getName());
    }

    @Test
    void update_unknownItemOrNotOwner_throwsNotFoundException() {
        ItemUpdateDto patch = ItemUpdateDto.builder().name("Big Drill").build();
        when(itemRepository.findByIdAndOwnerId(1L, 99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.update(1L, 99L, patch));
        verify(itemRepository, never()).save(any());
    }

    // --- search ---

    @Test
    void search_validUser_returnsMatchingItems() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(itemRepository.search("drill")).thenReturn(List.of(item));

        Collection<ItemDto> result = itemService.search("drill", 1L);

        assertEquals(1, result.size());
        assertEquals("Drill", result.iterator().next().getName());
    }

    @Test
    void search_emptyText_returnsEmptyList() {
        Collection<ItemDto> result = itemService.search("", 1L);

        assertTrue(result.isEmpty());
        verifyNoInteractions(itemRepository);
    }

    @Test
    void search_unknownUser_throwsNotFoundException() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> itemService.search("drill", 99L));
        verify(itemRepository, never()).search(any());
    }

    // --- addComment ---

    @Test
    void addComment_userWithCompletedBooking_savesAndReturnsCommentDto() {
        CommentDto request = CommentDto.builder().text("Works great").build();
        Comment saved = Comment.builder().id(5L).text("Works great").item(item).author(user)
                .created(LocalDateTime.now()).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(
                eq(1L), eq(1L), any(LocalDateTime.class))).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(saved);

        CommentDto result = itemService.addComment(1L, 1L, request);

        assertEquals("Works great", result.getText());
        assertEquals("Alice", result.getAuthorName());

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());
        assertEquals(item, captor.getValue().getItem());
        assertEquals(user, captor.getValue().getAuthor());
    }

    @Test
    void addComment_userWithoutCompletedBooking_throwsBadRequestException() {
        CommentDto request = CommentDto.builder().text("Works great").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(
                eq(1L), eq(1L), any(LocalDateTime.class))).thenReturn(false);

        assertThrows(BadRequestException.class, () -> itemService.addComment(1L, 1L, request));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void addComment_unknownUser_throwsNotFoundException() {
        CommentDto request = CommentDto.builder().text("Works great").build();
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addComment(1L, 99L, request));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void addComment_unknownItem_throwsNotFoundException() {
        CommentDto request = CommentDto.builder().text("Works great").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.addComment(99L, 1L, request));
        verify(commentRepository, never()).save(any());
    }
}
