package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.User;

@Data
@Builder
public class ItemRequestDto {

    private Long id;
    private String description;
    private User requestor;

}
