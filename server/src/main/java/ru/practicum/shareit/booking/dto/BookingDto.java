package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.BookedItemDto;
import ru.practicum.shareit.user.dto.UserBookerDto;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {

    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Long itemId;

    private Status status;

    private BookedItemDto item;

    private UserBookerDto booker;

}
