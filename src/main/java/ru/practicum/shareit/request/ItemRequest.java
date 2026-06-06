package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequest {

    public Long id;
    public String description;
    public User requestor;
    public LocalDateTime created;

}
