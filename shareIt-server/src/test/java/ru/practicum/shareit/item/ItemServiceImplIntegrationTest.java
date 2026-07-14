package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemsDto;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private RequestRepository requestRepository;

    private User owner;
    private User booker;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(User.builder().name("Alice").email("alice@example.com").build());
        booker = userRepository.save(User.builder().name("Bob").email("bob@example.com").build());
    }

    private Item saveItem(User itemOwner, String name, boolean available) {
        return itemRepository.save(Item.builder()
                .owner(itemOwner)
                .name(name)
                .description(name + " description")
                .available(available)
                .build());
    }

    private Booking saveBooking(Item item, User user, LocalDateTime start, LocalDateTime end, Status status) {
        return bookingRepository.save(Booking.builder()
                .item(item)
                .booker(user)
                .start(start)
                .end(end)
                .status(status)
                .build());
    }

    private Comment saveComment(Item item, User author, String text) {
        return commentRepository.save(Comment.builder()
                .item(item)
                .author(author)
                .text(text)
                .created(LocalDateTime.now())
                .build());
    }

    @Test
    void getAll_returnsOwnerItemsWithBookingsAndComments() {
        Item itemWithBooking = saveItem(owner, "Дрель", true);
        Item plainItem = saveItem(owner, "Отвёртка", true);
        saveItem(booker, "Молоток", true);

        Booking booking = saveBooking(itemWithBooking, booker,
                LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), Status.APPROVED);
        saveComment(itemWithBooking, booker, "Отличная дрель");

        Collection<ItemsDto> result = itemService.getAll(owner.getId());

        assertEquals(2, result.size());

        ItemsDto bookedDto = result.stream()
                .filter(dto -> dto.getId().equals(itemWithBooking.getId()))
                .findFirst().orElseThrow();
        assertEquals(booking.getStart(), bookedDto.getStart());
        assertEquals(booking.getEnd(), bookedDto.getEnd());
        assertEquals(1, bookedDto.getComments().size());
        assertEquals("Отличная дрель", bookedDto.getComments().get(0).getText());

        ItemsDto plainDto = result.stream()
                .filter(dto -> dto.getId().equals(plainItem.getId()))
                .findFirst().orElseThrow();
        assertNull(plainDto.getStart());
        assertTrue(plainDto.getComments().isEmpty());
    }

    @Test
    void getItemById_forOwner_returnsLastAndNextBookingAndComments() {
        Item item = saveItem(owner, "Дрель", true);
        Booking lastBooking = saveBooking(item, booker,
                LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), Status.APPROVED);
        Booking nextBooking = saveBooking(item, booker,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Status.APPROVED);
        saveComment(item, booker, "Отличная дрель");

        ItemDto result = itemService.getItemById(item.getId(), owner.getId());

        assertEquals("Дрель", result.getName());
        assertEquals(lastBooking.getStart(), result.getLastBooking());
        assertEquals(nextBooking.getStart(), result.getNextBooking());
        assertEquals(1, result.getComments().size());
        assertEquals("Bob", result.getComments().get(0).getAuthorName());
    }

    @Test
    void add_savesItemLinkedToRequest() {
        Request request = requestRepository.save(Request.builder()
                .description("Нужна дрель")
                .requestor(booker)
                .created(LocalDateTime.now())
                .build());
        ItemDto dto = ItemDto.builder()
                .name("Дрель")
                .description("Аккумуляторная дрель")
                .available(true)
                .requestId(request.getId())
                .build();

        ItemDto created = itemService.add(dto, owner.getId());

        assertNotNull(created.getId());
        assertEquals(request.getId(), created.getRequestId());
        Item saved = itemRepository.findById(created.getId()).orElseThrow();
        assertEquals("Дрель", saved.getName());
        assertEquals(owner.getId(), saved.getOwner().getId());
        assertEquals(request.getId(), saved.getRequest().getId());
    }

    @Test
    void update_updatesOnlyProvidedFields() {
        Item item = saveItem(owner, "Дрель", true);
        ItemUpdateDto patch = ItemUpdateDto.builder().description("Новое описание").available(false).build();

        ItemUpdateDto result = itemService.update(item.getId(), owner.getId(), patch);

        assertEquals("Дрель", result.getName());
        assertEquals("Новое описание", result.getDescription());
        assertFalse(result.getAvailable());
        Item saved = itemRepository.findById(item.getId()).orElseThrow();
        assertEquals("Дрель", saved.getName());
        assertEquals("Новое описание", saved.getDescription());
        assertFalse(saved.getAvailable());
    }

    @Test
    void addComment_afterFinishedBooking_savesCommentToDatabase() {
        Item item = saveItem(owner, "Дрель", true);
        saveBooking(item, booker,
                LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), Status.APPROVED);
        CommentDto dto = CommentDto.builder().text("Всё отлично").build();

        CommentDto created = itemService.addComment(item.getId(), booker.getId(), dto);

        assertNotNull(created.getId());
        assertEquals("Bob", created.getAuthorName());
        List<Comment> saved = commentRepository.findAllByItemId(item.getId());
        assertEquals(1, saved.size());
        assertEquals("Всё отлично", saved.get(0).getText());
        assertEquals(booker.getId(), saved.get(0).getAuthor().getId());
    }

    @Test
    void addComment_withoutFinishedBooking_throwsBadRequestException() {
        Item item = saveItem(owner, "Дрель", true);
        CommentDto dto = CommentDto.builder().text("Всё отлично").build();

        assertThrows(BadRequestException.class, () -> itemService.addComment(item.getId(), booker.getId(), dto));
        assertTrue(commentRepository.findAllByItemId(item.getId()).isEmpty());
    }

    @Test
    void search_returnsOnlyAvailableItemsMatchingText() {
        Item drill = saveItem(owner, "Дрель", true);
        saveItem(owner, "Дрель старая", false);
        saveItem(owner, "Отвёртка", true);

        Collection<ItemDto> result = itemService.search("дрель", booker.getId());

        assertEquals(1, result.size());
        assertEquals(drill.getId(), result.iterator().next().getId());
    }

}
