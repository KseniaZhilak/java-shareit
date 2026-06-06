package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {

    public Long id;
    public LocalDateTime start;
    public LocalDateTime end;
    public Item item;
    public User booker;
    public Status status;

}
