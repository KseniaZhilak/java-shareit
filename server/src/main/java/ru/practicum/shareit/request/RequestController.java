package ru.practicum.shareit.request;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
public class RequestController {

    private final RequestServiceImpl requestService;

    public RequestController(RequestServiceImpl requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public CreateRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody CreateRequestDto createDto) {
        return requestService.createRequest(userId, createDto);
    }

    @GetMapping
    public List<RequestDto> getRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getOwnRequests(userId);
    }

    @GetMapping("/{requestId}")
    public RequestDto getRequestById(@PathVariable long requestId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getRequestById(requestId, userId);
    }

    @GetMapping("/all")
    public List<RequestDto> getRequestsAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.getAllRequests(userId);
    }

}
