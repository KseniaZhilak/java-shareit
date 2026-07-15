package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.BookedItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserBookerDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    private final LocalDateTime start = LocalDateTime.of(2026, 8, 1, 12, 0, 0);
    private final LocalDateTime end = LocalDateTime.of(2026, 8, 2, 12, 0, 0);

    private final BookingDto bookingDto = BookingDto.builder()
            .id(1L)
            .itemId(1L)
            .start(start)
            .end(end)
            .status(Status.WAITING)
            .item(BookedItemDto.builder().id(1L).name("Дрель").build())
            .booker(UserBookerDto.builder().id(2L).build())
            .build();

    @Test
    void createBooking_returnsCreatedBooking() throws Exception {
        User booker = User.builder().id(2L).name("Bob").email("bob@example.com").build();
        Item item = Item.builder().id(1L).owner(booker).name("Дрель").description("Дрель").available(true).build();
        BookingCreateDto created = BookingCreateDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(Status.WAITING)
                .build();
        when(bookingService.createBooking(any(BookingDto.class), eq(2L))).thenReturn(created);

        BookingDto request = BookingDto.builder().itemId(1L).start(start).end(end).build();

        mvc.perform(post("/bookings")
                        .header(USER_HEADER, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.item.name").value("Дрель"))
                .andExpect(jsonPath("$.booker.id").value(2));
    }

    @Test
    void updateBooking_returnsApprovedBooking() throws Exception {
        BookingUpdateDto updated = BookingUpdateDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .status(Status.APPROVED)
                .item(BookedItemDto.builder().id(1L).name("Дрель").build())
                .booker(UserBookerDto.builder().id(2L).build())
                .build();
        when(bookingService.updateBooking(1L, 1L, true)).thenReturn(updated);

        mvc.perform(patch("/bookings/1")
                        .header(USER_HEADER, 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBookingById_returnsBooking() throws Exception {
        when(bookingService.getBookingById(1L, 2L)).thenReturn(bookingDto);

        mvc.perform(get("/bookings/1").header(USER_HEADER, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.itemId").value(1))
                .andExpect(jsonPath("$.booker.id").value(2));
    }

    @Test
    void getBookingById_unknownBooking_returnsNotFound() throws Exception {
        when(bookingService.getBookingById(99L, 2L)).thenThrow(new NotFoundException("Booking not found"));

        mvc.perform(get("/bookings/99").header(USER_HEADER, 2L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Booking not found"));
    }

    @Test
    void getAllByUser_defaultState_returnsBookings() throws Exception {
        when(bookingService.getAllByUser(2L, State.ALL)).thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings").header(USER_HEADER, 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getAllByUser_withStateParam_passesStateToService() throws Exception {
        when(bookingService.getAllByUser(2L, State.WAITING)).thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .header(USER_HEADER, 2L)
                        .param("state", "WAITING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }

    @Test
    void getAllByOwner_returnsBookingsOfOwnerItems() throws Exception {
        when(bookingService.getAllByOwner(1L, State.ALL)).thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner").header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].item.id").value(1));
    }

}
