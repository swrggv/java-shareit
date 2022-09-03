package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoForItemOwner;

import java.util.List;

@Data
@AllArgsConstructor
public class ItemDtoWithDate {
    private long id;

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;

    private BookingDtoForItemOwner lastBooking;

    private BookingDtoForItemOwner nextBooking;

    private List<CommentDto> comments;

    public ItemDtoWithDate() {
    }
}
