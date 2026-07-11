package ru.practicum.shareit.request;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.request.dto.AnswerDto;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.time.LocalDateTime;
import java.util.List;

public class RequestMapper {

    public static Request toRequest(CreateRequestDto createDto) {
        return Request.builder()
                .description(createDto.getDescription())
                .created(LocalDateTime.now())
                .build();
    }

    public static RequestDto toRequestDto(Request request, List<Item> items) {
        return RequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(items.stream()
                        .map(RequestMapper::toAnswerDto)
                        .toList())
                .build();
    }

    public static RequestDto toRequestDto(Request request, Item item) {
        return RequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(List.of(toAnswerDto(item)))
                .build();
    }

    private static AnswerDto toAnswerDto(Item item) {
        return AnswerDto.builder()
                .item(item.getId())
                .name(item.getName())
                .requestor(item.getOwner().getId())
                .build();
    }

    public static CreateRequestDto toCreatedRequestDto(Request request) {
        return CreateRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .build();
    }

}
