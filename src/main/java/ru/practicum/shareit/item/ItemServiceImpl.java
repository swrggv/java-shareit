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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;

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
        List<ItemDtoWithDate> result = addCommentsToItems(List.of(item));
        if (item.getOwner().getId() != ownerId) {
            return result.get(0);
        } else {
            return addBookingDtoForItemOwner(result.get(0));
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

    private List<ItemDtoWithDate> addCommentsToItems(List<Item> items) {
        Map<Item, List<Comment>> comments = commentRepository.findByItemIn(items, Sort.by(DESC, "created"))
                .stream()
                .collect(groupingBy(Comment::getItem, toList()));
        List<ItemDtoWithDate> itemsDto = new ArrayList<>();
        for (Item item : comments.keySet()) {
            ItemDtoWithDate itemDtoWithDate =
                    createItemDtoWithDateWithComments(item, comments.getOrDefault(item, new ArrayList<>()));
            itemsDto.add(itemDtoWithDate);
        }
        return itemsDto;

        /*List<Comment> comments = commentRepository.findByItemId(item.getId());
        item.setComments(CommentMapper.toListCommentsDto(comments));*/
    }

    private ItemDtoWithDate createItemDtoWithDateWithComments(Item item, List<Comment> comments) {
        ItemDtoWithDate itemDtoWithDate = ItemMapper.toItemDtoWithDate(item);
        List<CommentDto> commentsDto = new ArrayList<>();
        if (comments.size() != 0) {
            commentsDto = CommentMapper.toListCommentsDto(comments);
        }
        itemDtoWithDate.setComments(commentsDto);
        return itemDtoWithDate;
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
