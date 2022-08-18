package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item addItem(Item item);

    Item updateItem(Item item);

    Item getItemById(long itemId);

    List<Item> getAllItemsOfOwner(long userId);

    List<Item> getAllItemsAvailableToRent(String text);
}
