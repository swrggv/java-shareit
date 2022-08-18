package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

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
    public ItemDto addItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        ItemDto result = itemService.addItem(itemDto, userId);
        log.info("Item was added {}", itemDto);
        return result;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody Map<Object, Object> fields,
                              @PathVariable Long itemId,
                              @RequestHeader("X-Sharer-User-Id") long userId) {
        ItemDto result = itemService.updateItem(fields, itemId, userId);
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
