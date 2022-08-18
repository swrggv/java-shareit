package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Map;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, long userId);

    ItemDto updateItem(Map<Object, Object> fields, long itemId, long userId);

    ItemDto getItemEachUserById(long itemId);

    List<ItemDto> getAllItemsOfOwner(long userId);

    List<ItemDto> getItemsAvailableToRent(String text);
}
