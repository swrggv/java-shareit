package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ItemDto addItem(ItemDto itemDto, long userId) {
        Optional<User> optionalOwner = userRepository.findById(userId);
        if (optionalOwner.isPresent()) {
            User owner = optionalOwner.get();
            Item result = itemRepository.save(ItemMapper.toItem(itemDto, owner, null));
            return ItemMapper.toItemDto(result);
        } else {
            throw new ModelNotFoundException(String.format("User %s not found", userId));
        }
    }

    @Override
    public ItemDto updateItem(ItemDto patchItem, long itemId, long userId) {
        if (isOwner(itemId, userId)) {
            Optional<User> user = userRepository.findById(userId);
            Optional<Item> oldItem = itemRepository.findById(itemId);
            Item updatedItem = patch(oldItem.get(), ItemMapper.toItem(patchItem, user.get(), null));
            return ItemMapper.toItemDto(itemRepository.save(updatedItem));
        } else {
            throw new NoRootException(String.format("Access is forbidden. User %s doesn't have access rights", userId));
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDtoWithDate getItemEachUserById(long itemId, long ownerId) {
        Optional<Item> item = itemRepository.findById(itemId);
        ItemDtoWithDate result = ItemMapper
                .toItemDtoWithDate(item.orElseThrow(() -> new ModelNotFoundException(String.format("Item %s not found",
                        itemId))));
        addCommentsToItems(List.of(result));
        /*List<CommentDto> comment = commentRepository.findByItem(item.get()).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
        result.setComments(comment);*/
        if (item.get().getOwner().getId() != ownerId) {
            return result;
        } else {
            return addBookingDtoForItemOwner(result);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDtoWithDate> getAllItemsOfOwner(long userId) {
        Optional<User> owner = userRepository.findById(userId);
        List<ItemDtoWithDate> items = itemRepository.findByOwner(owner.orElseThrow(() ->
                        new ModelNotFoundException(String.format("User %d not found", userId)))).stream()
                .map(ItemMapper::toItemDtoWithDate)
                .collect(Collectors.toList());
        addCommentsToItems(items);
        for (ItemDtoWithDate item : items) {
            addBookingDtoForItemOwner(item);
        }
        Collections.sort(items, (a, b) -> a.getId() < b.getId() ? -1 : a.getId() == b.getId() ? 0 : 1);
        return items;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getItemsAvailableToRent(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository.findByNameOrDescription(text);
        return items.stream().map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addCommentToItem(long itemId, long userId, CommentDto commentDto) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new ModelNotFoundException("Item or User not found");
        }
        if (bookingRepository.isExists(itemId, userId, LocalDateTime.now())) {
            Comment comment = new Comment();
            Optional<User> author = userRepository.findById(userId);
            comment.setItem(item.get());
            comment.setAuthor(author.get());
            comment.setText(commentDto.getText());
            comment.setCreated(LocalDateTime.now());
            commentDto = CommentMapper.toCommentDto(commentRepository.save(comment));
        } else {
            throw new ValidationException(String.format("User %s did not book item %s", userId, item.get().getId()));
        }
        return commentDto;
    }

    private ItemDtoWithDate addBookingDtoForItemOwner(ItemDtoWithDate itemDtoWithDate) {
        Booking lastBooking = bookingRepository.findBookingByItemWithDateBefore(itemDtoWithDate.getId(),
                LocalDateTime.now());
        Booking nextBooking = bookingRepository.findBookingByItemWithDateAfter(itemDtoWithDate.getId(),
                LocalDateTime.now());
        if (lastBooking != null) {
            itemDtoWithDate.setLastBooking(BookingMapper.toBookingDtoForItemOwner(lastBooking));
        }
        if (nextBooking != null) {
            itemDtoWithDate.setNextBooking(BookingMapper.toBookingDtoForItemOwner(nextBooking));
        }
        return itemDtoWithDate;
    }

    private void addCommentsToItems(List<ItemDtoWithDate> items) {
        List<Comment> comments;
        for (ItemDtoWithDate item : items) {
            comments = commentRepository.findByItemId(item.getId());
            item.setComments(comments.stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList()));
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

    private boolean isOwner(long itemId, long userId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ModelNotFoundException(String.format("User %s not found", userId)))
                .getOwner().getId() == userId;
    }
}
