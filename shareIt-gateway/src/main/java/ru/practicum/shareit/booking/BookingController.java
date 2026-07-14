package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient bookingClient;

    public BookingController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestBody @Valid BookingDto booking,
                                                @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingClient.createBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@PathVariable("bookingId") long id,
                                                @RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam Boolean approved) {
        return bookingClient.updateBooking(id, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable("bookingId") long id,
                                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingClient.getBookingById(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam(name = "state", defaultValue = "ALL") State state) {
        return bookingClient.getAllByUser(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam(name = "state", defaultValue = "ALL") State state) {
        return bookingClient.getAllByOwner(userId, state);
    }

}
