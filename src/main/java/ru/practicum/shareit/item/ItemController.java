package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDate;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@Validated
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

    //ЭТОТ
    @GetMapping("/{itemId}")
    public ItemDtoWithDate getItemEachUser(@PathVariable long itemId,
                                           @RequestHeader("X-Sharer-User-Id") long ownerId) {
        ItemDtoWithDate result = itemService.getItemEachUserById(itemId, ownerId);
        log.info("Get item {}", result);
        return result;
    }

    @GetMapping
    public List<ItemDtoWithDate> getItemOwnerUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(value = "from", required = false, defaultValue = "0") @Min(0) int from,
                                                  @RequestParam(value = "size", required = false, defaultValue = "20") int size) {
        List<ItemDtoWithDate> result = itemService.getAllItemsOfOwner(userId, from, size);
        log.info("Get all user's {} items", userId);
        return result;
    }

    @GetMapping("/search")
    public List<ItemDto> getItemAvailableToRenter(@RequestParam String text,
                                                  @RequestParam(value = "from", required = false, defaultValue = "0") @Min(0) int from,
                                                  @RequestParam(value = "size", required = false, defaultValue = "20") int size) {
        List<ItemDto> result = itemService.getItemsAvailableToRent(text, from, size);
        log.info("Get available items with {}", text);
        return result;
    }

    //пошли комменты
    @PostMapping("/{itemId}/comment")
    public CommentDto addCommentToItem(@Validated(Create.class) @RequestBody CommentDto commentDto,
                                       @PathVariable long itemId,
                                       @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.addCommentToItem(itemId, userId, commentDto);
    }

}
