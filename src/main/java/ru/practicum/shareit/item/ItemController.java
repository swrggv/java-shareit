package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addItem(@Validated(Create.class) @RequestBody ItemDto itemDto,
                           @RequestHeader("X-Sharer-User-Id") long userId) {
        ItemDto result = itemService.addItem(itemDto, userId);
        log.info("Item was added {}", itemDto);
        return result;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto patchItem,
                              @PathVariable Long itemId,
                              @RequestHeader("X-Sharer-User-Id") long userId) {
        ItemDto result = itemService.updateItem(patchItem, itemId, userId);
        log.info("Item {} was changed", itemId);
        return result;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemEachUser(@PathVariable long itemId) {
        ItemDto result = itemService.getItemEachUserById(itemId);
        log.info("Get item {}", result);
        return result;
    }

    @GetMapping
    public List<ItemDto> getItemOwnerUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        List<ItemDto> result = itemService.getAllItemsOfOwner(userId);
        log.info("Get all user's {} items", userId);
        return result;
    }

    @GetMapping("/search")
    public List<ItemDto> getItemAvailableToRenter(@RequestParam String text) {
        List<ItemDto> result = itemService.getItemsAvailableToRent(text);
        log.info("Get available items with {}", text);
        return result;
    }
}
