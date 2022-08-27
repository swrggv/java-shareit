package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto fields, long itemId, long userId);

    ItemDto getItemEachUserById(long itemId);

    List<ItemDto> getAllItemsOfOwner(long userId);

    List<ItemDto> getItemsAvailableToRent(String text);
}
