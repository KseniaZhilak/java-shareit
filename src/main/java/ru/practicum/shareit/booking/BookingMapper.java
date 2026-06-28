package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingUpdateDto;
import ru.practicum.shareit.item.Item;

import static ru.practicum.shareit.item.ItemMapper.toBookedItemDto;
import static ru.practicum.shareit.user.UserMapper.toUserBookerDto;

public class BookingMapper {

    public static BookingCreateDto toBookingCreateDto(Booking booking) {
        return BookingCreateDto.builder()
                .id(booking.getId())
                .item(booking.getItem())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBooking(BookingDto bookingDto, Item itemBooked) {
        return Booking.builder()
                .item(itemBooked)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();
    }

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .build();
    }

    public static BookingUpdateDto toBookingUpdateDto(Booking booking) {
        return BookingUpdateDto.builder()
                .id(booking.getId())
                .item(toBookedItemDto(booking.getItem()))
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(toUserBookerDto(booking.getBooker()))
                .build();
    }

}
