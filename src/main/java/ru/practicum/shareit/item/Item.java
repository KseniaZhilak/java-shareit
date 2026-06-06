package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
@Builder
public class Item {

    public Long id;
    public User owner;
    private String name;
    private String description;
    private Boolean isAvailable;
    private ItemRequest request;

}
