package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemRequestDto {
    private long id;
    private String description;
    private LocalDateTime created = LocalDateTime.now();
    private long requestorId;
    private List<ItemDto> items = new ArrayList<>();

    public ItemRequestDto(long id, String description, long requestorId, List<ItemDto> items) {
        this.id = id;
        this.description = description;
        this.requestorId = requestorId;
        this.items = items;
    }

    public ItemRequestDto(String description, LocalDateTime created, long requestorId, List<ItemDto> items) {
        this.description = description;
        this.created = created;
        this.requestorId = requestorId;
        this.items = items;
    }
}
