package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;

import java.util.*;

@Repository
public class ItemStorageInMemory implements ItemStorage {

    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Optional<Item> getItemById(long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Collection<Item> getAll(long userId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .toList();
    }

    @Override
    public Item create(Item item) {
        Long id = getNextId();
        item.setId(id);
        items.put(id, item);
        return item;
    }

    @Override
    public Item update(long id, long userId, Item itemUpdateDto) {
        Item item = Optional.ofNullable(items.get(id))
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("access denied for editing item");
        }

        if (itemUpdateDto.getAvailable() != null) {
            item.setAvailable(itemUpdateDto.getAvailable());
        }

        if (itemUpdateDto.getDescription() != null) {
            item.setDescription(itemUpdateDto.getDescription());
        }

        if (itemUpdateDto.getName() != null) {
            item.setName(itemUpdateDto.getName());
        }

        return item;
    }

    @Override
    public Collection<Item> search(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }

        String search = text.toLowerCase();
        return items.values()
                .stream()
                .filter(item ->
                        item.getDescription().toLowerCase().contains(search) ||
                                item.getName().toLowerCase().contains(search))
                .filter(Item::getAvailable)
                .toList();
    }

    private long nextId = 1;

    private long getNextId() {
        return nextId++;
    }
}
