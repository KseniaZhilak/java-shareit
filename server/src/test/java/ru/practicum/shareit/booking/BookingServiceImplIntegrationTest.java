package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User owner;
    private User booker;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = userRepository.save(User.builder().name("Alice").email("alice@example.com").build());
        booker = userRepository.save(User.builder().name("Bob").email("bob@example.com").build());
        item = itemRepository.save(Item.builder()
                .owner(owner)
                .name("Дрель")
                .description("Аккумуляторная дрель")
                .available(true)
                .build());
    }

    private Booking saveBooking(Item bookedItem, User user, LocalDateTime start, LocalDateTime end, Status status) {
        return bookingRepository.save(Booking.builder()
                .item(bookedItem)
                .booker(user)
                .start(start)
                .end(end)
                .status(status)
                .build());
    }

    @Test
    void createBooking_savesBookingWithWaitingStatus() {
        BookingDto dto = BookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        BookingCreateDto created = bookingService.createBooking(dto, booker.getId());

        assertNotNull(created.getId());
        assertEquals(Status.WAITING, created.getStatus());
        Booking saved = bookingRepository.findById(created.getId()).orElseThrow();
        assertEquals(booker.getId(), saved.getBooker().getId());
        assertEquals(item.getId(), saved.getItem().getId());
        assertEquals(Status.WAITING, saved.getStatus());
    }

    @Test
    void createBooking_unknownUser_throwsNotFoundException() {
        BookingDto dto = BookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(dto, 9999L));
    }

    @Test
    void createBooking_unknownItem_throwsNotFoundException() {
        BookingDto dto = BookingDto.builder()
                .itemId(9999L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThrows(NotFoundException.class, () -> bookingService.createBooking(dto, booker.getId()));
    }

    @Test
    void createBooking_unavailableItem_throwsBadRequestException() {
        item.setAvailable(false);
        itemRepository.save(item);
        BookingDto dto = BookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThrows(BadRequestException.class, () -> bookingService.createBooking(dto, booker.getId()));
    }

    @Test
    void updateBooking_approvedByOwner_changesStatusInDatabase() {
        Booking booking = saveBooking(item, booker,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Status.WAITING);

        BookingUpdateDto result = bookingService.updateBooking(booking.getId(), owner.getId(), true);

        assertEquals(Status.APPROVED, result.getStatus());
        Booking saved = bookingRepository.findById(booking.getId()).orElseThrow();
        assertEquals(Status.APPROVED, saved.getStatus());
        assertFalse(itemRepository.findById(item.getId()).orElseThrow().getAvailable());
    }

    @Test
    void updateBooking_rejectedByOwner_changesStatusToRejected() {
        Booking booking = saveBooking(item, booker,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Status.WAITING);

        BookingUpdateDto result = bookingService.updateBooking(booking.getId(), owner.getId(), false);

        assertEquals(Status.REJECTED, result.getStatus());
        Booking saved = bookingRepository.findById(booking.getId()).orElseThrow();
        assertEquals(Status.REJECTED, saved.getStatus());
        assertTrue(itemRepository.findById(item.getId()).orElseThrow().getAvailable());
    }

    @Test
    void updateBooking_unknownUser_throwsBadRequestException() {
        Booking booking = saveBooking(item, booker,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Status.WAITING);

        assertThrows(BadRequestException.class,
                () -> bookingService.updateBooking(booking.getId(), 9999L, true));
    }

    @Test
    void updateBooking_notOwner_throwsNotFoundException() {
        Booking booking = saveBooking(item, booker,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Status.WAITING);

        assertThrows(NotFoundException.class,
                () -> bookingService.updateBooking(booking.getId(), booker.getId(), true));
    }

    @Test
    void getBookingById_returnsBookingForBooker() {
        Booking booking = saveBooking(item, booker,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Status.WAITING);

        BookingDto result = bookingService.getBookingById(booking.getId(), booker.getId());

        assertEquals(booking.getId(), result.getId());
        assertEquals(item.getId(), result.getItemId());
        assertEquals(booker.getId(), result.getBooker().getId());
    }

    @Test
    void getBookingById_returnsBookingForItemOwner() {
        Booking booking = saveBooking(item, booker,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Status.WAITING);

        BookingDto result = bookingService.getBookingById(booking.getId(), owner.getId());

        assertEquals(booking.getId(), result.getId());
        assertEquals(item.getId(), result.getItemId());
    }

    @Test
    void getBookingById_unknownBooking_throwsNotFoundException() {
        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(9999L, booker.getId()));
    }

    @Test
    void getBookingById_strangerUser_throwsNotFoundException() {
        User stranger = userRepository.save(User.builder().name("Carol").email("carol@example.com").build());
        Booking booking = saveBooking(item, booker,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Status.WAITING);

        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(booking.getId(), stranger.getId()));
    }

    @Test
    void getAllByUser_returnsBookerBookingsSortedByStartDesc() {
        Booking oldBooking = saveBooking(item, booker,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Status.WAITING);
        Booking recentBooking = saveBooking(item, booker,
                LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), Status.REJECTED);

        List<BookingDto> result = List.copyOf(bookingService.getAllByUser(booker.getId(), State.ALL));

        assertEquals(2, result.size());
        assertEquals(recentBooking.getId(), result.get(0).getId());
        assertEquals(oldBooking.getId(), result.get(1).getId());

        Collection<BookingDto> waiting = bookingService.getAllByUser(booker.getId(), State.WAITING);
        assertEquals(1, waiting.size());
        assertEquals(oldBooking.getId(), waiting.iterator().next().getId());
    }

    @Test
    void getAllByUser_filtersByState() {
        Booking pastApproved = saveBooking(item, booker,
                LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(2), Status.APPROVED);
        Booking futureWaiting = saveBooking(item, booker,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Status.WAITING);
        Booking futureRejected = saveBooking(item, booker,
                LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), Status.REJECTED);

        Collection<BookingDto> current = bookingService.getAllByUser(booker.getId(), State.CURRENT);
        assertEquals(1, current.size());
        assertEquals(pastApproved.getId(), current.iterator().next().getId());

        Collection<BookingDto> past = bookingService.getAllByUser(booker.getId(), State.PAST);
        assertEquals(1, past.size());
        assertEquals(pastApproved.getId(), past.iterator().next().getId());

        Collection<BookingDto> future = bookingService.getAllByUser(booker.getId(), State.FUTURE);
        assertEquals(2, future.size());

        Collection<BookingDto> rejected = bookingService.getAllByUser(booker.getId(), State.REJECTED);
        assertEquals(1, rejected.size());
        assertEquals(futureRejected.getId(), rejected.iterator().next().getId());

        Collection<BookingDto> waiting = bookingService.getAllByUser(booker.getId(), State.WAITING);
        assertEquals(1, waiting.size());
        assertEquals(futureWaiting.getId(), waiting.iterator().next().getId());
    }

    @Test
    void getAllByUser_unknownUser_throwsNotFoundException() {
        assertThrows(NotFoundException.class, () -> bookingService.getAllByUser(9999L, State.ALL));
    }

    @Test
    void getAllByOwner_unknownUser_throwsNotFoundException() {
        assertThrows(NotFoundException.class, () -> bookingService.getAllByOwner(9999L, State.ALL));
    }

    @Test
    void getAllByOwner_returnsBookingsOfOwnerItems() {
        Booking booking = saveBooking(item, booker,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Status.WAITING);

        Collection<BookingDto> result = bookingService.getAllByOwner(owner.getId(), State.ALL);

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.iterator().next().getId());
        assertTrue(bookingService.getAllByOwner(booker.getId(), State.ALL).isEmpty());
    }

}
