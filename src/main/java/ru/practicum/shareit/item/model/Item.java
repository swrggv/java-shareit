package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.requests.ItemRequest;

import javax.validation.constraints.NotEmpty;

/**
 * // TODO .
 */
@Data
public class Item {
    private long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String description;

    private boolean available;

    private String owner;

    private ItemRequest request;
}
