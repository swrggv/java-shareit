package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ModelNotFoundException;
import ru.practicum.shareit.exception.NoRootException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemDto addItem(ItemDto itemDto, long userId) {
        User owner = fromOptionalToUser(userId);
        Item result = itemRepository.save(ItemMapper.toItem(itemDto, owner, null));
        return ItemMapper.toItemDto(result);
    }

    @Transactional
    @Override
    public ItemDto updateItem(ItemDto patchItem, long itemId, long userId) {
        if (isOwner(itemId, userId)) {
            User user = fromOptionalToUser(userId);
            Item oldItem = fromOptionalToItem(itemId);
            Item updatedItem = patch(oldItem, ItemMapper.toItem(patchItem, user, null));
            return ItemMapper.toItemDto(itemRepository.save(updatedItem));
        } else {
            throw new NoRootException(String.format("Access is forbidden. User %s doesn't have access rights", userId));
        }
    }

    @Override
    public ItemDtoWithDate getItemEachUserById(long itemId, long ownerId) {
        Item item = fromOptionalToItem(itemId);
        ItemDtoWithDate result = ItemMapper.toItemDtoWithDate(item);
        addCommentsToItems(List.of(result));
        if (item.getOwner().getId() != ownerId) {
            return result;
        } else {
            return addBookingDtoForItemOwner(result);
        }
    }

    @Override
    public List<ItemDtoWithDate> getAllItemsOfOwner(long userId) {
        User owner = fromOptionalToUser(userId);
        List<ItemDtoWithDate> items = ItemMapper.toListItemDtoWithDate(itemRepository.findByOwner(owner));
        for (ItemDtoWithDate item : items) {
            addBookingDtoForItemOwner(item);
        }
        Collections.sort(items, (a, b) -> a.getId() < b.getId() ? -1 : a.getId() == b.getId() ? 0 : 1);
        return items;
    }

    @Override
    public List<ItemDto> getItemsAvailableToRent(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository.findByNameOrDescription(text);
        return items.stream().map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto addCommentToItem(long itemId, long userId, CommentDto commentDto) {
        Item item = fromOptionalToItem(itemId);
        if (bookingRepository.isExists(itemId, userId, LocalDateTime.now())) {
            User author = fromOptionalToUser(userId);
            Comment comment = CommentMapper.toComment(commentDto, item, author);
            commentDto = CommentMapper.toCommentDto(commentRepository.save(comment));
        } else {
            throw new ValidationException(String.format("User %s did not book item %s", userId, item.getId()));
        }
        return commentDto;
    }

    private ItemDtoWithDate addBookingDtoForItemOwner(ItemDtoWithDate itemDtoWithDate) {
        Booking lastBooking = bookingRepository.findBookingByItemWithDateBefore(itemDtoWithDate.getId(),
                LocalDateTime.now());
        Booking nextBooking = bookingRepository.findBookingByItemWithDateAfter(itemDtoWithDate.getId(),
                LocalDateTime.now());
        if (lastBooking != null) {
            itemDtoWithDate.setLastBooking(BookingMapper.toItemDtoWithDateToBookingDto(lastBooking));
        }
        if (nextBooking != null) {
            itemDtoWithDate.setNextBooking(BookingMapper.toItemDtoWithDateToBookingDto(nextBooking));
        }
        return itemDtoWithDate;
    }

    private void addCommentsToItems(List<ItemDtoWithDate> items) {
        List<Comment> comments;
        for (ItemDtoWithDate item : items) {
            comments = commentRepository.findByItemId(item.getId());
            item.setComments(CommentMapper.toListCommentsDto(comments));
        }
    }

    private Item fromOptionalToItem(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ModelNotFoundException(String.format("Item %s not found", itemId)));
    }

    private User fromOptionalToUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ModelNotFoundException(String.format("User %s not found", userId)));
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

    private boolean isOwner(long itemId, long userId) {
        return fromOptionalToItem(itemId).getOwner().getId() == userId;
    }
}
