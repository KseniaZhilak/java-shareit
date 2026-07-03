package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemsDto {

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

    @FutureOrPresent
    private LocalDateTime start;

    @Future
    private LocalDateTime end;

    private List<CommentDto> comments;

}
