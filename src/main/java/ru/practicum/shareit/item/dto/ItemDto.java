package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDto {

    private Long id;

    @NotNull(message = "Поле name обязательно")
    @NotBlank
    private String name;

    @NotNull(message = "Поле description обязательно")
    @NotBlank
    private String description;

    @NotNull(message = "Поле isAvailable обязательно")
    private Boolean available;

    private Long request;

}
