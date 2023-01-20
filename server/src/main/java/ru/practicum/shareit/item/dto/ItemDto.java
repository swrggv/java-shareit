package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private long id;
    @NotBlank(message = "Item name can not be empty")
    private String name;
    @NotBlank(message = "Item description can not be empty")
    private String description;
    @NotNull(message = "Item Available can not be empty")
    private Boolean available;
    private Long requestId;
}
