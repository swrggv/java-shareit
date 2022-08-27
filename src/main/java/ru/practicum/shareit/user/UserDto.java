package ru.practicum.shareit.user;

import lombok.Data;
import ru.practicum.shareit.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UserDto {

    private long id;

    @NotBlank(groups = Create.class)
    private String name;

    @NotBlank(groups = Create.class)
    @Pattern(regexp = "^(.+)@(\\S+)$", groups = Create.class)
    private String email;

    public UserDto() {
    }
}
