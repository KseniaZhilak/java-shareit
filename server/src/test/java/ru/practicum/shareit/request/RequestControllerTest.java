package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RequestController.class)
class RequestControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private RequestServiceImpl requestService;

    private final RequestDto requestDto = RequestDto.builder()
            .id(1L)
            .description("Нужна дрель")
            .created(LocalDateTime.of(2026, 7, 14, 12, 30, 45))
            .items(List.of(ItemRequestDto.builder().item(5L).name("Дрель").requestor(2L).build()))
            .build();

    @Test
    void createRequest_returnsCreatedRequest() throws Exception {
        CreateRequestDto created = CreateRequestDto.builder()
                .id(1L)
                .description("Нужна дрель")
                .created(LocalDateTime.of(2026, 7, 14, 12, 30, 45))
                .build();
        when(requestService.createRequest(eq(1L), any(CreateRequestDto.class))).thenReturn(created);

        mvc.perform(post("/requests")
                        .header(USER_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Нужна дрель"))
                .andExpect(jsonPath("$.created").value("2026-07-14T12:30:45"));
    }

    @Test
    void getRequests_returnsOwnRequests() throws Exception {
        when(requestService.getOwnRequests(1L)).thenReturn(List.of(requestDto));

        mvc.perform(get("/requests").header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].items[0].name").value("Дрель"));
    }

    @Test
    void getRequestById_returnsRequest() throws Exception {
        when(requestService.getRequestById(1L, 1L)).thenReturn(requestDto);

        mvc.perform(get("/requests/1").header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Нужна дрель"))
                .andExpect(jsonPath("$.items[0].item").value(5));
    }

    @Test
    void getRequestById_unknownRequest_returnsNotFound() throws Exception {
        when(requestService.getRequestById(99L, 1L)).thenThrow(new NotFoundException("Request not found"));

        mvc.perform(get("/requests/99").header(USER_HEADER, 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Request not found"));
    }

    @Test
    void getRequestsAll_returnsOtherUsersRequests() throws Exception {
        when(requestService.getAllRequests(1L)).thenReturn(List.of(requestDto));

        mvc.perform(get("/requests/all").header(USER_HEADER, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].description").value("Нужна дрель"));
    }

}
