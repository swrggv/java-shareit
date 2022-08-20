package ru.practicum.shareit.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UserDto {

    private long id;

    @NotBlank(groups = UserCreate.class)
    private String name;

    @NotBlank(groups = UserCreate.class)
    @Pattern(regexp = "^(.+)@(\\S+)$", groups = UserCreate.class)
    private String email;

    public UserDto() {
    }
}
