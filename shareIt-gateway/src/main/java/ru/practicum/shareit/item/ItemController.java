package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.getAll(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable("itemId") long id,
                                              @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.getItemById(id, userId);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestBody @Valid ItemDto itemDto,
                                      @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.add(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable("itemId") long id,
                                             @RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestBody @Valid CommentDto comment) {
        return itemClient.addComment(id, userId, comment);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable("itemId") long id,
                                         @RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody ItemUpdateDto itemUpdateDto) {
        return itemClient.update(id, userId, itemUpdateDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text,
                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.search(text, userId);
    }

}
