package ru.practicum.shareit.request;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateRequestDto;

@RestController
@RequestMapping(path = "/requests")
public class RequestController {

    private final RequestClient requestClient;

    public RequestController(RequestClient requestClient) {
        this.requestClient = requestClient;
    }

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody CreateRequestDto createDto) {
        return requestClient.createRequest(userId, createDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestClient.getOwnRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable long requestId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return requestClient.getRequestById(requestId, userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestClient.getAllRequests(userId);
    }
}
