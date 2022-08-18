package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import ru.practicum.shareit.exception.ModelNotFoundExсeption;
import ru.practicum.shareit.exception.NoRootException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto addItem(ItemDto itemDto, long userId) {
        if (isUserExist(userId)) {
            Item item = ItemMapper.toItem(itemDto);
            item.setOwner(userId);
            item.setId(IdItemGeneration.createId());
            itemRepository.addItem(item);
            return ItemMapper.toItemDto(item);
        } else {
            log.error("User not found {}", userId);
            throw new ModelNotFoundExсeption(String.format("User %s not found", userId));
        }

    }

    @Override
    public ItemDto updateItem(Map<Object, Object> fields, long itemId, long userId) {
        if (isOwner(itemId, userId)) {
            Item item = getItemById(itemId);
            fields.forEach((k, v) -> {
                Field field = ReflectionUtils.findField(Item.class, String.valueOf(k));
                field.setAccessible(true);
                ReflectionUtils.setField(field, item, v);
            });
            itemRepository.updateItem(item);
            return ItemMapper.toItemDto(item);
        } else {
            log.error("Access is forbidden. User {} doesn't have access rights", userId);
            throw new NoRootException(String.format("Access is forbidden. User %s doesn't have access rights", userId));
        }
    }

    private Item getItemById(long itemId) {
        return itemRepository.getItemById(itemId);
    }

    @Override
    public ItemDto getItemEachUserById(long itemId) {
        Item item = itemRepository.getItemById(itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllItemsOfOwner(long userId) {
        List<Item> items = itemRepository.getAllItemsOfOwner(userId);
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsAvailableToRent(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository.getAllItemsAvailableToRent(text);
        return items.stream().map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private boolean isUserExist(long userId) {
        User user = userRepository.getUser(userId);
        return userRepository.getAllUsers().contains(user);
    }

    private boolean isOwner(long itemId, long userId) {
        return itemRepository.getItemById(itemId).getOwner() == userId;
    }

    static class IdItemGeneration {
        private static long id = 0;

        public static long createId() {
            return ++id;
        }
    }
}
