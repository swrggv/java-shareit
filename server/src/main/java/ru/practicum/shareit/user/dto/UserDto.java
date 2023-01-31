package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private long id;
    @NotBlank(message = "Name has to be not empty")
    private String name;
    @Email(message = "Email has to be correct")
    @NotBlank(message = "Email has to be not empty")
    private String email;
}
