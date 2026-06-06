package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    Collection<ItemDto> getAll(long userId);

    ItemDto getItemById(long id, long userId);

    ItemDto add(ItemDto itemDto, long userId);

    void delete(long id, long userId);

    ItemDto update(long id, long userId);

    ItemDto search(String text, long userId);


}
