package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.dto.ItemsDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("Дрель")
            .description("Аккумуляторная дрель")
            .available(true)
            .comments(List.of())
            .build();

    @Test
    void getAll_returnsOwnerItems() throws Exception {
        ItemsDto itemsDto = ItemsDto.builder()
                .id(1L)
                .name("Дрель")
                .description("Аккумуляторная дрель")
                .available(true)
                .comments(List.of())
                .build();
        when(itemService.getAll(1L)).thenReturn(List.of(itemsDto));

        mvc.perform(get("/items").header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Дрель"));
    }

    @Test
    void getAll_withoutUserHeader_returnsBadRequest() throws Exception {
        mvc.perform(get("/items"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getItemById_returnsItem() throws Exception {
        when(itemService.getItemById(1L, 1L)).thenReturn(itemDto);

        mvc.perform(get("/items/1").header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Аккумуляторная дрель"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void getItemById_unknownItem_returnsNotFound() throws Exception {
        when(itemService.getItemById(99L, 1L)).thenThrow(new NotFoundException("Item not found"));

        mvc.perform(get("/items/99").header(USER_HEADER, 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Item not found"));
    }

    @Test
    void add_returnsCreatedItem() throws Exception {
        when(itemService.add(any(ItemDto.class), eq(1L))).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header(USER_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Дрель"));
    }

    @Test
    void addComment_returnsCreatedComment() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("Отличная дрель")
                .authorName("Bob")
                .created(LocalDateTime.of(2026, 7, 14, 12, 30, 45))
                .build();
        when(itemService.addComment(eq(1L), eq(2L), any(CommentDto.class))).thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .header(USER_HEADER, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Отличная дрель"))
                .andExpect(jsonPath("$.authorName").value("Bob"));
    }

    @Test
    void addComment_withoutFinishedBooking_returnsBadRequest() throws Exception {
        CommentDto commentDto = CommentDto.builder().text("Отличная дрель").build();
        when(itemService.addComment(eq(1L), eq(2L), any(CommentDto.class)))
                .thenThrow(new BadRequestException("User has not completed a booking for this item"));

        mvc.perform(post("/items/1/comment")
                        .header(USER_HEADER, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_returnsUpdatedItem() throws Exception {
        ItemUpdateDto patch = ItemUpdateDto.builder().name("Новая дрель").build();
        ItemUpdateDto updated = ItemUpdateDto.builder()
                .name("Новая дрель")
                .description("Аккумуляторная дрель")
                .available(true)
                .build();
        when(itemService.update(eq(1L), eq(1L), any(ItemUpdateDto.class))).thenReturn(updated);

        mvc.perform(patch("/items/1")
                        .header(USER_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(patch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Новая дрель"));
    }

    @Test
    void search_returnsMatchingItems() throws Exception {
        when(itemService.search("дрель", 1L)).thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .header(USER_HEADER, 1L)
                        .param("text", "дрель"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Дрель"));
    }

}
