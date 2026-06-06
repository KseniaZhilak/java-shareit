package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemStorageInMemory;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserStorageInMemory;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.item.ItemMapper.toItem;
import static ru.practicum.shareit.item.ItemMapper.toItemDto;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemStorageInMemory itemStorageInMemory;
    private final UserStorageInMemory userStorageInMemory;

    public ItemServiceImpl(ItemStorageInMemory itemStorageInMemory, UserStorageInMemory userStorageInMemory) {
        this.itemStorageInMemory = itemStorageInMemory;
        this.userStorageInMemory = userStorageInMemory;
    }

    @Override
    public Collection<ItemDto> getAll(long userId) {
        return List.of();
    }

    @Override
    public ItemDto getItemById(long id, long userId) {
        return null;
    }

    @Override
    public ItemDto add(ItemDto itemDto, long userId) {
        Optional<User> user = userStorageInMemory.getUserById(userId);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }

        Item item = toItem(itemDto);
        item.setOwner(user.get());
        Item itemCreated = itemStorageInMemory.create(item);
        return toItemDto(itemCreated);
    }

    @Override
    public void delete(long id, long userId) {

    }

    @Override
    public ItemDto update(long id, long userId) {
        return null;
    }

    @Override
    public ItemDto search(String text, long userId) {
        return null;
    }
}
