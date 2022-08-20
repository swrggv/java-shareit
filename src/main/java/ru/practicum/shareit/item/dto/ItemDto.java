package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ItemDto {

    private long id;

    @NotBlank(groups = ItemCreate.class)
    private String name;

    @NotBlank(groups = ItemCreate.class)
    private String description;

    @NotNull(groups = ItemCreate.class)
    private Boolean available;

    private Long requestId;

    public ItemDto() {
    }
}
