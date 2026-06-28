package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;

import java.util.Collection;

public interface BookingService {

    BookingCreateDto createBooking(BookingDto booking, long userId);

    BookingUpdateDto updateBooking(long id, long userId, Boolean approved);

    BookingDto getBookingById(long id, long userId);

    Collection<BookingDto> getAllByUser(long userId);

    Collection<BookingDto> getAllByOwner(long userId);

}
