package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {

    public Long id;
    public String name;
    public String email;

}
