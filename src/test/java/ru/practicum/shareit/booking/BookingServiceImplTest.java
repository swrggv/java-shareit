package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.ModelNotFoundException;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = {"db.name=test"})
class BookingServiceImplTest {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    private Item item;
    private User booker;
    private User owner;
    private User anotherUser;
    private BookingDto bookingDto;
    private BookingDto anotherBookingDto;
    private BookItemRequestDto bookItemRequestDto;
    private final LocalDateTime start = LocalDateTime.parse("2100-09-01T01:00");
    private final LocalDateTime end = LocalDateTime.parse("2110-09-01T01:00");

    @BeforeEach
    void prepare() {
        owner = new User("owner", "owner@gmail.com");
        booker = new User("booker", "booker@gmail.com");
        anotherUser = new User("another", "another@gmail.com");
        owner = UserMapper.toUser(userService.addUser(UserMapper.toUserDto(owner)));
        booker = UserMapper.toUser(userService.addUser(UserMapper.toUserDto(booker)));
        anotherUser = UserMapper.toUser(userService.addUser(UserMapper.toUserDto(anotherUser)));

        item = new Item("item", "item description", true, owner, null);
        item = ItemMapper.toItem(itemService.addItem(ItemMapper.toItemDto(item), owner.getId()), owner, null);

        bookItemRequestDto = new BookItemRequestDto(start, end, item.getId());
        bookingDto = bookingService.addBooking(bookItemRequestDto, booker.getId());
    }

    @Test
    void addBooking() {
        assertThat(bookingDto.getId()).isNotZero();
        assertThat(bookingDto.getBooker().getId()).isEqualTo(booker.getId());
        assertThat(bookingDto.getBooker().getName()).isEqualTo(booker.getName());
        assertThat(bookingDto.getStart()).isEqualTo(start);
        assertThat(bookingDto.getEnd()).isEqualTo(end);
        assertThat(bookingDto.getItem().getId()).isEqualTo(item.getId());
        assertThat(bookingDto.getItem().getName()).isEqualTo(item.getName());
        assertThat(bookingDto.getStatus()).isEqualTo(Status.WAITING);
    }

    @Test
    void addBooking_WrongDate() {
        LocalDateTime start = LocalDateTime.parse("2000-09-01T01:00");
        LocalDateTime end = LocalDateTime.parse("1000-09-01T01:00");
        bookItemRequestDto.setStart(start);
        bookItemRequestDto.setEnd(end);
        ValidationException ex = assertThrows(ValidationException.class,
                () -> bookingService.addBooking(bookItemRequestDto, booker.getId()));
        assertThat(ex.getMessage()).contains("End date should not be before start date");
    }

    @Test
    void addBooking_NotAvailable() {
        item.setAvailable(false);
        ItemDto addedItem = itemService.addItem(ItemMapper.toItemDto(item), anotherUser.getId());
        ValidationException ex = assertThrows(ValidationException.class,
                () -> bookingService.addBooking(bookItemRequestDto, booker.getId()));
        assertThat(ex.getMessage()).contains(String.format("Item %s is not available", addedItem.getId()));
    }

    @Test
    void addBooking_BookerIsOwner() {
        ModelNotFoundException ex = assertThrows(ModelNotFoundException.class,
                () -> bookingService.addBooking(bookItemRequestDto, owner.getId()));
        assertThat(ex.getMessage()).contains("User can not book his own item");
    }

    @Test
    void approveBooking() {
        bookingDto.setStatus(Status.APPROVED);
        assertEquals(bookingDto, bookingService.approveBooking(bookingDto.getId(), true, owner.getId()));
    }

    @Test
    void getBookingByIdIfOwnerOrBooker() {
        assertEquals(bookingDto, bookingService.getBookingByIdIfOwnerOrBooker(bookingDto.getId(), owner.getId()));
    }

    @Test
    void getBookingByIdIfOwnerOrBooker_UserNotFound() {
        ModelNotFoundException ex = assertThrows(ModelNotFoundException.class,
                () -> bookingService.getBookingByIdIfOwnerOrBooker(-1, owner.getId()));
        assertThat(ex.getMessage()).contains("User or booking not found");
    }

    @Test
    void getBookingByIdIfOwnerOrBooker_BookingNotFound() {
        ModelNotFoundException ex = assertThrows(ModelNotFoundException.class,
                () -> bookingService.getBookingByIdIfOwnerOrBooker(bookingDto.getId(), anotherUser.getId()));
        assertThat(ex.getMessage()).contains("Booking not found");
    }

    @Test
    void getBookingByUserSorted_UserNotFound() {
        ModelNotFoundException ex = assertThrows(ModelNotFoundException.class,
                () -> bookingService.getBookingByUserSorted(-1, State.ALL, 0, 2));
        assertThat(ex.getMessage()).contains("User not found");
    }

    @Test
    void getBookingByUserSorted_AllState() {
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingByUserSorted(booker.getId(), State.ALL, 0, 2));
    }

    @Test
    void getBookingByUserSorted_CurrentState() {
        LocalDateTime start = LocalDateTime.parse("1000-09-01T01:00");
        LocalDateTime end = LocalDateTime.parse("2100-09-01T01:00");
        bookItemRequestDto = new BookItemRequestDto(start, end, item.getId());
        bookingDto = bookingService.addBooking(bookItemRequestDto, booker.getId());
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingByUserSorted(booker.getId(), State.CURRENT, 0, 2));
    }

    @Test
    void getBookingByUserSorted_PastState() {
        LocalDateTime start = LocalDateTime.parse("1000-09-01T01:00");
        LocalDateTime end = LocalDateTime.parse("1500-09-01T01:00");
        bookItemRequestDto = new BookItemRequestDto(start, end, item.getId());
        bookingDto = bookingService.addBooking(bookItemRequestDto, booker.getId());
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingByUserSorted(booker.getId(), State.PAST, 0, 2));
    }

    @Test
    void getBookingByUserSorted_FutureState() {
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingByUserSorted(booker.getId(), State.FUTURE, 0, 2));
    }

    @Test
    void getBookingByUserSorted_WaitingStatus() {
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingByUserSorted(booker.getId(), State.WAITING, 0, 2));
    }

    @Test
    void getBookingByUserSorted_RejectedStatus() {
        bookingDto = bookingService.approveBooking(bookingDto.getId(), false, owner.getId());
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingByUserSorted(booker.getId(), State.REJECTED, 0, 2));
    }

    @Test
    void getBookingByUserSorted_UnsupportedState() {
        UnknownStateException ex = assertThrows(UnknownStateException.class,
                () -> bookingService.getBookingByUserSorted(booker.getId(),
                        State.UNSUPPORTED_STATUS, 0, 2));
        assertThat(ex.getMessage()).contains("Unknown state: UNSUPPORTED_STATUS");
    }

    @Test
    void getBookingByItemOwner_OwnerNotFound() {
        ModelNotFoundException ex = assertThrows(ModelNotFoundException.class,
                () -> bookingService.getBookingByItemOwner(-1, State.ALL, 0, 2));
        assertThat(ex.getMessage()).contains("User not found");
    }

    @Test
    void getBookingByItemOwner_AllState() {
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingByItemOwner(owner.getId(), State.ALL, 0, 2));
    }

    @Test
    void getBookingByItemOwner_CurrentState() {
        LocalDateTime start = LocalDateTime.parse("1000-09-01T01:00");
        LocalDateTime end = LocalDateTime.parse("2100-09-01T01:00");
        bookItemRequestDto = new BookItemRequestDto(start, end, item.getId());
        bookingDto = bookingService.addBooking(bookItemRequestDto, booker.getId());
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingByItemOwner(owner.getId(), State.CURRENT, 0, 2));
    }

    @Test
    void getBookingByItemOwner_PastState() {
        LocalDateTime start = LocalDateTime.parse("1000-09-01T01:00");
        LocalDateTime end = LocalDateTime.parse("1500-09-01T01:00");
        bookItemRequestDto = new BookItemRequestDto(start, end, item.getId());
        bookingDto = bookingService.addBooking(bookItemRequestDto, booker.getId());
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingByItemOwner(owner.getId(), State.PAST, 0, 2));
    }

    @Test
    void getBookingByItemOwner_FutureState() {
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingByItemOwner(owner.getId(), State.FUTURE, 0, 2));
    }

    @Test
    void getBookingByItemOwner_WaitingStatus() {
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingByItemOwner(owner.getId(), State.WAITING, 0, 2));
    }

    @Test
    void getBookingByItemOwner_RejectedStatus() {
        bookingDto = bookingService.approveBooking(bookingDto.getId(), false, owner.getId());
        List<BookingDto> bookings = List.of(bookingDto);
        assertEquals(bookings, bookingService.getBookingByItemOwner(owner.getId(), State.REJECTED, 0, 2));
    }

    @Test
    void getBookingByItemOwner_UnsupportedState() {
        UnknownStateException ex = assertThrows(UnknownStateException.class,
                () -> bookingService.getBookingByItemOwner(owner.getId(),
                        State.UNSUPPORTED_STATUS, 0, 2));
        assertThat(ex.getMessage()).contains("Unknown state: UNSUPPORTED_STATUS");
    }
}