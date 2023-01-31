package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@TestPropertySource(properties = "application.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceImplTest {

    @Autowired
    private BookingService bookingService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    private BookItemRequestDto input;
    private BookingDto output;
    private User owner;
    private User booker;
    private Item item;
    private static final LocalDateTime START = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime END = START.plusDays(2);

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@gmail.com")
                .build();
        booker = User.builder()
                .id(2L)
                .name("booker")
                .email("booker@gmail.com")
                .build();
        item = Item.builder()
                .id(1L)
                .name("item")
                .available(true)
                .description("item 1")
                .owner(owner)
                .build();
        input = BookItemRequestDto.builder()
                .itemId(1L)
                .start(START)
                .end(END)
                .build();
        output = BookingDto.builder()
                .id(1L)
                .start(START)
                .end(END)
                .item(new BookingDto.ItemBooking(item.getId(), item.getName()))
                .booker(new BookingDto.Booker(booker.getId(), booker.getName()))
                .status(Status.WAITING)
                .build();
        userRepository.save(owner);
        userRepository.save(booker);
        itemRepository.save(item);
    }

    @Test
    void addBooking() {
        BookingDto result = bookingService.addBooking(input, booker.getId());
        assertThat(result, equalTo(output));
    }

    @Test
    void approveBooking() {
        bookingService.addBooking(input, booker.getId());
        BookingDto result = bookingService.approveBooking(1L, true, owner.getId());
        assertThat(result.getStatus(), is(Status.APPROVED));
    }

    @Test
    void getBookingByIdIfOwnerOrBooker() {
        bookingService.addBooking(input, booker.getId());
        BookingDto result = bookingService.getBookingByIdIfOwnerOrBooker(output.getId(), owner.getId());
        assertThat(result.getId(), equalTo(output.getId()));
    }

    @Test
    void getBookingByIdIfOwnerOrBookerWithModelNotFoundException() {
        assertThatThrownBy(() -> bookingService.getBookingByIdIfOwnerOrBooker(output.getId(), booker.getId()));
    }

    @Test
    void getBookingByUserSorted() {
        bookingService.addBooking(input, booker.getId());
        List<BookingDto> result = bookingService.getBookingByUserSorted(booker.getId(), State.ALL, 0, 2);
        assertThat(result, hasSize(1));
    }

    @Test
    void getBookingByItemOwner() {
        bookingService.addBooking(input, booker.getId());
        List<BookingDto> result = bookingService.getBookingByItemOwner(owner.getId(), State.ALL, 0, 2);
        assertThat(result, hasSize(1));
    }
}