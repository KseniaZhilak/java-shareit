package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ItemStorageInMemory {

    private final Map<Long, Item> items = new HashMap<>();

    public Item create(Item item) {
        return items.put(item.getId(), item);
    }

}
