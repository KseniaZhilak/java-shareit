package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface RequestService {

    CreateRequestDto createRequest(Long userId, CreateRequestDto createDto);

    List<RequestDto> getOwnRequests(Long userId);

    List<RequestDto> getAllRequests(Long userId);

    RequestDto getRequestById(Long id, Long userId);
}
