package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CreateRequestDto {

    private Long id;

    @NotNull(message = "Описание не может быть пустым")
    @NotBlank
    private String description;

    private LocalDateTime created;

}

