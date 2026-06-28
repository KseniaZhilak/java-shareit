package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingCreateDto createBooking(@RequestBody @Valid BookingDto booking, @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.createBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingUpdateDto updateBooking(@PathVariable("bookingId") long id, @RequestHeader("X-Sharer-User-Id") long userId, @RequestParam Boolean approved) {
        return bookingService.updateBooking(id, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable("bookingId") long id, @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getBookingById(id, userId);
    }

}
