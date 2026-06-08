package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserStorage;

import java.util.Collection;
import java.util.Optional;

import static ru.practicum.shareit.item.ItemMapper.*;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Collection<ItemDto> getAll(long userId) {
        Optional<User> user = userStorage.getUserById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        Collection<Item> all = itemStorage.getAll(userId);
        return all.stream().map(ItemMapper::toItemDto).toList();
    }

    @Override
    public ItemDto getItemById(long id, long userId) {
        if (userStorage.getUserById(userId).isEmpty()) {
            throw new NotFoundException("User not found");
        }

        Optional<Item> item = itemStorage.getItemById(id);
        if (item.isEmpty()) {
            throw new NotFoundException("Item not found");
        }
        return toItemDto(item.get());
    }

    @Override
    public ItemDto add(ItemDto itemDto, long userId) {
        Optional<User> user = userStorage.getUserById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        Item item = toItem(itemDto);
        item.setOwner(user.get());
        Item itemCreated = itemStorage.create(item);
        return toItemDto(itemCreated);
    }

    @Override
    public ItemUpdateDto update(long id, long userId, ItemUpdateDto patch) {
        Optional<User> user = userStorage.getUserById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        Item updated = itemStorage.update(id, userId, toItem(patch));
        return toItemUpdateDto(updated);
    }

    @Override
    public void delete(long id, long userId) {
    }

    @Override
    public Collection<ItemDto> search(String text, long userId) {
        if (userStorage.getUserById(userId).isEmpty()) {
            throw new NotFoundException("User not found");
        }

        return itemStorage.search(text).stream().map(ItemMapper::toItemDto).toList();
    }
}
