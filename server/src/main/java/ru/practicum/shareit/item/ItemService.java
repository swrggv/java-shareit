package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDate;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto fields, long itemId, long userId);

    ItemDtoWithDate getItemEachUserById(long itemId, long ownerId);

    List<ItemDtoWithDate> getAllItemsOfOwner(long userId, int from, int size);

    List<ItemDto> getItemsAvailableToRent(String text, int from, int size);

    CommentDto addCommentToItem(long itemId, long userId, CommentDto commentDto);
}
