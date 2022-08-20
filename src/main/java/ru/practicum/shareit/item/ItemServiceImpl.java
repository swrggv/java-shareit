package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.IdGenerator;
import ru.practicum.shareit.exception.ModelNotFoundException;
import ru.practicum.shareit.exception.NoRootException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final IdGenerator idGenerator;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, IdGenerator idGenerator) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.idGenerator = idGenerator;
    }

    @Override
    public ItemDto addItem(ItemDto itemDto, long userId) {
        if (isUserExist(userId)) {
            Item item = ItemMapper.toItem(itemDto, userId, null);
            item.setId(idGenerator.getId());
            itemRepository.addItem(item);
            return ItemMapper.toItemDto(item);
        } else {
            log.error("User not found {}", userId);
            throw new ModelNotFoundException(String.format("User %s not found", userId));
        }

    }

    @Override
    public ItemDto updateItem(ItemDto patchItem, long itemId, long userId) {
        if (isOwner(itemId, userId)) {
            Item oldItem = getItemById(itemId);
            Item result = patch(oldItem, ItemMapper.toItem(patchItem, userId, null));
            ItemDto itemDto = ItemMapper.toItemDto(result);
            itemRepository.updateItem(result);
            return itemDto;
        } else {
            log.error("Access is forbidden. User {} doesn't have access rights", userId);
            throw new NoRootException(String.format("Access is forbidden. User %s doesn't have access rights", userId));
        }
    }

    private Item patch(Item item, Item patchItem) {
        if (patchItem.getName() != null) {
            item.setName(patchItem.getName());
        }
        if (patchItem.getDescription() != null) {
            item.setDescription(patchItem.getDescription());
        }
        if (patchItem.getAvailable() != null) {
            item.setAvailable(patchItem.getAvailable());
        }
        return item;
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
}
