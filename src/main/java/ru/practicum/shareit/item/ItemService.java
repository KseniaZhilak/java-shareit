package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemsDto;

import java.util.Collection;

public interface ItemService {

    Collection<ItemsDto> getAll(long userId);

    ItemDto getItemById(long id, long userId);

    ItemDto add(ItemDto itemDto, long userId);

    ItemUpdateDto update(long id, long userId, ItemUpdateDto itemUpdateDto);

    Collection<ItemDto> search(String text, long userId);


}
