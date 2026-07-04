package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemsDto;

import java.util.Collection;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public Collection<ItemsDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getAll(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable("itemId") long id, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItemById(id, userId);
    }

    @PostMapping
    public ItemDto add(@RequestBody @Valid ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.add(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable("itemId") long id, @RequestHeader("X-Sharer-User-Id") long userId,
                                 @RequestBody @Valid CommentDto comment) {
        return itemService.addComment(id, userId, comment);
    }

    @PatchMapping("/{itemId}")
    public ItemUpdateDto update(@PathVariable("itemId") long id, @RequestHeader("X-Sharer-User-Id") long userId,
                                @RequestBody ItemUpdateDto itemUpdateDto) {
        return itemService.update(id, userId, itemUpdateDto);
    }

    @GetMapping("/search")
    public Collection<ItemDto> search(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.search(text, userId);
    }

}
