package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import ru.practicum.shareit.requests.ItemRequestRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public ItemDto addItem(ItemDto itemDto, long userId) {
        User owner = fromOptionalToUser(userId);
        Item result = itemRepository.save(ItemMapper.toItem(itemDto, owner, checkItemRequest(itemDto)));
        return ItemMapper.toItemDto(result);
    }

    @Transactional
    @Override
    public ItemDto updateItem(ItemDto patchItem, long itemId, long userId) {
        if (isOwner(itemId, userId)) {
            User user = fromOptionalToUser(userId);
            Item item = fromOptionalToItem(itemId);
            patch(item, ItemMapper.toItem(patchItem, user, null));
            return ItemMapper.toItemDto(item);
        } else {
            throw new NoRootException(String.format("Access is forbidden. User %s doesn't have access rights", userId));
        }
    }

    @Override
    public ItemDtoWithDate getItemEachUserById(long itemId, long ownerId) {
        Item item = fromOptionalToItem(itemId);
        ItemDtoWithDate result = ItemMapper.toItemDtoWithDate(item);
        addCommentsToItems(result);
        if (item.getOwner().getId() != ownerId) {
            return result;
        } else {
            return addBookingDtoForItemOwner(result);
        }
    }

    @Override
    public List<ItemDtoWithDate> getAllItemsOfOwner(long userId, int from, int size) {
        User owner = fromOptionalToUser(userId);
        int page = getPageNumber(from, size);
        List<ItemDtoWithDate> items = ItemMapper.toListItemDtoWithDate(
                itemRepository.findByOwner(PageRequest.of(page, size, Sort.by("id")), owner));
        for (ItemDtoWithDate item : items) {
            addBookingDtoForItemOwner(item);
        }
        return items;
    }

    @Override
    public List<ItemDto> getItemsAvailableToRent(String text, int from, int size) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        int page = getPageNumber(from, size);
        List<Item> items = itemRepository.findByNameOrDescription(PageRequest.of(page, size), text);
        return items.stream().map(ItemMapper::toItemDto)
                .collect(toList());
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

    private ItemRequest checkItemRequest(ItemDto itemDto) {
        return itemDto.getRequestId() != null ? fromOptionalToRequest(itemDto.getRequestId()) : null;
    }

    private ItemRequest fromOptionalToRequest(long requestId) {
        return itemRequestRepository.findById(requestId).orElseThrow(() ->
                new ModelNotFoundException(String.format("Request %d not found", requestId)));
    }

    private int getPageNumber(int from, int size) {
        return from / size;
    }

    private void addCommentsToItems(ItemDtoWithDate item) {
        /*Map<Item, List<Comment>> comments = commentRepository.findByItemIn(items, Sort.by(DESC, "created"))
                .stream()
                .map()
                .collect(groupingBy(Comment::getItem, toList()));*/
        List<Comment> comments = commentRepository.findByItemId(item.getId());
        item.setComments(CommentMapper.toListCommentsDto(comments));
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
