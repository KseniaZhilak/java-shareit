package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.BookedItemDto;
import ru.practicum.shareit.user.dto.UserBookerDto;
import ru.practicum.shareit.validation.ValidBookingDates;

import java.time.LocalDateTime;

@Data
@Builder
@ValidBookingDates
public class BookingDto {

    @NotNull(message = "Дата начала аренды обязательна")
    @FutureOrPresent
    private LocalDateTime start;

    @NotNull(message = "Дата окончания аренды обязательна")
    @Future
    private LocalDateTime end;

    private Long itemId;

    private Status status;

    private BookedItemDto item;

    private UserBookerDto booker;

}
