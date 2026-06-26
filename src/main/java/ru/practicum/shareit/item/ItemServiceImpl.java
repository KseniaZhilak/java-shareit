package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dao.UserStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static ru.practicum.shareit.item.ItemMapper.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    public final UserRepository userRepository;

    @Override
    public Collection<ItemDto> getAll(long userId) {
        return itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public ItemDto getItemById(long id, long userId) {

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }

        Item item = itemRepository.findByIdAndOwnerId(id, userId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        return toItemDto(item);
    }

    @Override
    public ItemDto add(ItemDto itemDto, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item item = toItem(itemDto);
        item.setOwner(user);
        Item itemCreated = itemRepository.save(item);
        return toItemDto(itemCreated);
    }

    @Override
    public ItemUpdateDto update(long id, long userId, ItemUpdateDto patch) {

        Item item = itemRepository.findByIdAndOwnerId(id, userId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (patch.getName() != null) {
            item.setName(patch.getName());
        }

        if (patch.getDescription() != null) {
            item.setDescription(patch.getDescription());
        }

        if (patch.getAvailable() != null) {
            item.setAvailable(patch.getAvailable());
        }

        Item updated = itemRepository.save(item);
        return toItemUpdateDto(updated);
    }

    @Override
    public Collection<ItemDto> search(String text, long userId) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }

        return itemRepository.search(text)
                .stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto).toList();

    }
}
