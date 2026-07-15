package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemRequestDto {

    private Long item;
    private String name;
    private Long requestor;

}
