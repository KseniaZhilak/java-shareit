package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {

    private Long id;

    @NotNull(message = "Поле name обязательно")
    @NotBlank
    private String name;

    @NotNull(message = "Поле email обязательно")
    @NotBlank
    @Email
    private String email;

}
