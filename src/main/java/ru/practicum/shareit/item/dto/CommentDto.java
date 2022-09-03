package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.Create;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private long id;

    @NotEmpty(groups = Create.class, message = "Text can not be empty")
    private String text;

    private String authorName;

    private LocalDateTime created;
}
