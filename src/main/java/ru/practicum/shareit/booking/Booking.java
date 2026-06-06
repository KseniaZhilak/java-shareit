package ru.practicum.shareit.booking;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@Builder
public class Booking {

    public Long id;
    public LocalDateTime start;
    public LocalDateTime end;
    public Item item;
    public User booker;
    public Status status;

}
