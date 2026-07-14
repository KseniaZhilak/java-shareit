package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

import static ru.practicum.shareit.booking.BookingMapper.*;
import static ru.practicum.shareit.booking.Status.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
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

        if (Boolean.TRUE.equals(approved)) {
            item.setAvailable(false);
            booking.setStatus(APPROVED);
        } else {
            item.setAvailable(true);
            booking.setStatus(REJECTED);
        }
        bookingRepository.save(booking);
        itemRepository.save(item);

        return toBookingUpdateDto(booking);
    }

    @Override
    public BookingDto getBookingById(long id, long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        long bookerId = booking.getBooker().getId();
        long ownerId = booking.getItem().getOwner().getId();

        if (userId != bookerId && userId != ownerId) {
            throw new NotFoundException("Access denied: user is not the booker or item owner");
        }

        return toBookingDto(booking);
    }

    @Override
    public Collection<BookingDto> getAllByUser(long userId, State state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }

        List<Booking> bookings = bookingRepository.findAllByBookerId(userId);
        List<Booking> bookingsByFilter = searchByState(bookings, state);

        return bookingsByFilter.stream()
                .sorted((d1, d2) -> d2.getStart().compareTo(d1.getStart()))
                .map(BookingMapper::toBookingDto)
                .toList();
    }

    @Override
    public Collection<BookingDto> getAllByOwner(long userId, State state) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }

        List<Item> items = itemRepository.findAllByOwnerId(userId);

        List<Booking> bookings = bookingRepository.findAllByItemIdIn(items.stream().map(Item::getId).toList());
        List<Booking> bookingsByFilter = searchByState(bookings, state);

        return bookingsByFilter.stream().map(BookingMapper::toBookingDto).toList();
    }

    private List<Booking> searchByState(List<Booking> bookings, State state) {
        switch (state) {
            case WAITING:
                bookings = bookings.stream().filter(b -> b.getStatus() == WAITING).toList();
                break;
            case REJECTED:
                bookings = bookings.stream().filter(b -> b.getStatus() == REJECTED).toList();
                break;
            case CURRENT:
                bookings = bookings.stream().filter(b -> b.getStatus() == APPROVED).toList();
                break;
            case PAST:
                bookings = bookings.stream().filter(b -> b.getEnd().isBefore(LocalDateTime.now())).toList();
                break;
            case FUTURE:
                bookings = bookings.stream().filter(b -> b.getStart().isAfter(LocalDateTime.now())).toList();
                break;
            case ALL:
                break;
            default:
                throw new BadRequestException("Invalid state");
        }
        return bookings;
    }

}
