package ru.practicum.shareit.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UserDto {
    private long id;

    private String name;

    @NotBlank
    @Pattern(regexp = "^(.+)@(\\S+)$")
    private String email;

    public UserDto() {
    }
}
