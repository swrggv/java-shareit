package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
public class User {

    private long id;

    private String name;

    @NotBlank
    @Pattern(regexp = "^(.+)@(\\S+)$")
    private String email;

    public User() {
    }
}
