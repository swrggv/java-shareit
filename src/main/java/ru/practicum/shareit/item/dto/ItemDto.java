package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.requests.ItemRequest;

import javax.validation.constraints.NotEmpty;

/**
 * // TODO .
 */
@Data
@AllArgsConstructor
public class ItemDto {

    private long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String description;

    private boolean available;

    private String owner;

    private ItemRequest request;
}
