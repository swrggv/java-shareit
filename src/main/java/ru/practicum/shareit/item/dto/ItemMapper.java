package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static Item toItem(ItemDto itemDto, @Nullable User user, @Nullable ItemRequest request) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        item.setItemRequest(request);
        return item;
    }

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setRequestId(item.getItemRequest() != null ? item.getItemRequest().getId() : null);
        return itemDto;
    }

    public static ItemDtoWithDate toItemDtoWithDate(Item item) {
        ItemDtoWithDate itemDtoWithDate = new ItemDtoWithDate();
        itemDtoWithDate.setId(item.getId());
        itemDtoWithDate.setName(item.getName());
        itemDtoWithDate.setDescription(item.getDescription());
        itemDtoWithDate.setAvailable(item.getAvailable());
        itemDtoWithDate.setRequestId(item.getItemRequest() != null ? item.getItemRequest().getId() : null);
        return itemDtoWithDate;
    }

    public static List<ItemDtoWithDate> toListItemDtoWithDate(List<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemDtoWithDate)
                .collect(Collectors.toList());
    }
}
