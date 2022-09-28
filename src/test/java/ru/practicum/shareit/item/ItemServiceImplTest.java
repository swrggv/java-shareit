package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NoRootException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDate;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = {"db.name=test"})
class ItemServiceImplTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;
    private ItemDto itemDto;
    private UserDto userDto;

    @BeforeEach
    void prepare() {
        userDto = new UserDto("user", "user@gmail.com");
        userDto = userService.addUser(userDto);
        itemDto = new ItemDto("item1", "item description", true, null);
        itemDto = itemService.addItem(itemDto, userDto.getId());
    }

    @Test
    void addItem() {
        assertThat(itemDto.getName()).isEqualTo("item1");
        assertThat(itemDto.getDescription()).isEqualTo("item description");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getRequestId()).isNull();
    }

    @Test
    void updateItem_WithName() {
        ItemDto updatedItem = new ItemDto();
        updatedItem.setName("updated name");
        itemDto.setName("updated name");

        assertEquals(itemDto, itemService.updateItem(updatedItem, itemDto.getId(), userDto.getId()));
    }

    @Test
    void updateItem_WithDescription() {
        ItemDto updatedItem = new ItemDto();
        updatedItem.setDescription("updated description");
        itemDto.setDescription("updated description");

        assertEquals(itemDto, itemService.updateItem(updatedItem, itemDto.getId(), userDto.getId()));
    }

    @Test
    void updateItem_WithAvailable() {
        ItemDto updatedItem = new ItemDto();
        updatedItem.setAvailable(false);
        itemDto.setAvailable(false);

        assertEquals(itemDto, itemService.updateItem(updatedItem, itemDto.getId(), userDto.getId()));
    }

    @Test
    void updateItem_UserNotOwner() {
        ItemDto updatedItem = new ItemDto();
        updatedItem.setName("updated name");
        itemDto.setName("updated name");
        UserDto userNotOwner = new UserDto("notOwner", "notOwner@gmail.com");
        userNotOwner = userService.addUser(userNotOwner);
        long idUserNotOwner = userNotOwner.getId();

        NoRootException ex = assertThrows(NoRootException.class,
                () -> itemService.updateItem(updatedItem, itemDto.getId(), idUserNotOwner));
        assertTrue(ex.getMessage().contains(String.format("Access is forbidden. User %s doesn't have access rights",
                idUserNotOwner)));
    }

    @Test
    void getItemEachUserById_Owner() {
        User user = UserMapper.toUser(userDto);
        Item item = ItemMapper.toItem(itemDto, user, null);
        ItemDtoWithDate itemDtoWithDate = ItemMapper.toItemDtoWithDate(item);
        itemDtoWithDate.setComments(new ArrayList<>());

        assertEquals(itemDtoWithDate, itemService.getItemEachUserById(itemDto.getId(), userDto.getId()));
    }

    @Test
    void getItemEachUserById_Owner_WithNextBooking() {
        UserDto booker = new UserDto("booker", "booker@gmail.com");
        booker = userService.addUser(booker);

        LocalDateTime start = LocalDateTime.parse("2100-09-01T01:00");
        LocalDateTime end = LocalDateTime.parse("2200-09-01T01:00");

        BookItemRequestDto bookItemRequestDto = new BookItemRequestDto(start, end, itemDto.getId());
        BookingDto bookingDto = bookingService.addBooking(bookItemRequestDto, booker.getId());

        User owner = UserMapper.toUser(userDto);
        Item item = ItemMapper.toItem(itemDto, owner, null);

        ItemDtoWithDate itemDtoWithDate = ItemMapper.toItemDtoWithDate(item);
        itemDtoWithDate.setComments(new ArrayList<>());
        itemDtoWithDate.setNextBooking(new ItemDtoWithDate.BookingDto(bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                bookingDto.getBooker().getId()));

        assertEquals(itemDtoWithDate, itemService.getItemEachUserById(itemDto.getId(), userDto.getId()));
    }

    @Test
    void getItemEachUserById_Owner_WithLastBooking() {
        UserDto booker = new UserDto("booker", "booker@gmail.com");
        booker = userService.addUser(booker);

        LocalDateTime start = LocalDateTime.parse("1100-09-01T01:00");
        LocalDateTime end = LocalDateTime.parse("1200-09-01T01:00");

        BookItemRequestDto bookItemRequestDto = new BookItemRequestDto(start, end, itemDto.getId());
        BookingDto bookingDto = bookingService.addBooking(bookItemRequestDto, booker.getId());

        User owner = UserMapper.toUser(userDto);
        Item item = ItemMapper.toItem(itemDto, owner, null);

        ItemDtoWithDate itemDtoWithDate = ItemMapper.toItemDtoWithDate(item);
        itemDtoWithDate.setComments(new ArrayList<>());
        itemDtoWithDate.setLastBooking(new ItemDtoWithDate.BookingDto(bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                bookingDto.getBooker().getId()));

        assertEquals(itemDtoWithDate, itemService.getItemEachUserById(itemDto.getId(), userDto.getId()));
    }

    @Test
    void getItemEachUserById_NotOwner() {
        UserDto userNotOwner = new UserDto("notOwner", "notOwner@gmail.com");
        userNotOwner = userService.addUser(userNotOwner);

        User owner = UserMapper.toUser(userDto);
        Item item = ItemMapper.toItem(itemDto, owner, null);
        ItemDtoWithDate itemDtoWithDate = ItemMapper.toItemDtoWithDate(item);
        itemDtoWithDate.setComments(Collections.EMPTY_LIST);

        assertEquals(itemDtoWithDate, itemService.getItemEachUserById(itemDto.getId(), userNotOwner.getId()));
    }

    @Test
    void getAllItemsOfOwner() {
        User user = UserMapper.toUser(userDto);
        Item item = ItemMapper.toItem(itemDto, user, null);
        List<ItemDtoWithDate> itemDtoWithDateList = ItemMapper.toListItemDtoWithDate(List.of(item));

        assertEquals(itemDtoWithDateList, itemService.getAllItemsOfOwner(userDto.getId(), 0, 2));
    }

    @Test
    void getItemsAvailableToRent_BlankList() {
        assertEquals(Collections.EMPTY_LIST, itemService.getItemsAvailableToRent("", 0, 2));
    }

    @Test
    void getItemsAvailableToRent() {
        assertEquals(List.of(itemDto),
                itemService.getItemsAvailableToRent("ite", 0, 2));
    }

    @Test
    void addCommentToItem() {
        UserDto author = new UserDto("author", "author@gmail.com");
        author = userService.addUser(author);

        CommentDto commentDto = new CommentDto("comment", author.getName(), null);

        LocalDateTime start = LocalDateTime.parse("1100-09-01T01:00");
        LocalDateTime end = LocalDateTime.parse("1200-09-01T01:00");

        BookItemRequestDto bookItemRequestDto = new BookItemRequestDto(start, end, itemDto.getId());
        bookingService.addBooking(bookItemRequestDto, author.getId());

        CommentDto result = itemService.addCommentToItem(itemDto.getId(), author.getId(), commentDto);

        assertThat(result.getText()).isEqualTo("comment");
        assertThat(result.getAuthorName()).isEqualTo(author.getName());
    }

    @Test
    void addCommentToItem_UserDidntBook() {
        CommentDto commentDto = new CommentDto("comment", "author", null);
        ValidationException ex = assertThrows(ValidationException.class,
                () -> itemService.addCommentToItem(itemDto.getId(), userDto.getId(), commentDto));
        assertThat(ex.getMessage()).contains(
                String.format("User %s did not book item %s", userDto.getId(), itemDto.getId()));
    }
}