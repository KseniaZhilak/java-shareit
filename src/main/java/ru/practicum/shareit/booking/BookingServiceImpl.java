package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.booking.BookingMapper.*;
import static ru.practicum.shareit.booking.Status.APPROVED;
import static ru.practicum.shareit.booking.Status.WAITING;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    public final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingCreateDto createBooking(BookingDto bookingDto, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getAvailable()) {
            throw new BadRequestException("Item is not available");
        }

        Booking booking = toBooking(bookingDto, item);
        booking.setBooker(user);
        booking.setStatus(WAITING);

        Booking saved = bookingRepository.save(booking);

        return toBookingCreateDto(saved);
    }

    @Override
    public BookingUpdateDto updateBooking(long id, long userId, Boolean approved) {
        if (!userRepository.existsById(userId)) {
            throw new BadRequestException("Wrong user");
        }

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        Item item = itemRepository.findByIdAndOwnerId(booking.getItem().getId(), userId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if(approved) {
            item.setAvailable(false);
            booking.setStatus(APPROVED);
        } else {
            item.setAvailable(true);
            booking.setStatus(WAITING);
        }

        return toBookingUpdateDto(booking);
    }

    @Override
    public BookingDto getBookingById(long id, long userId) {
        return null;
    }

    @Override
    public Collection<BookingDto> getAllByUser(long userId) {
        return List.of();
    }

    @Override
    public Collection<BookingDto> getAllByOwner(long userId) {
        return List.of();
    }
}
