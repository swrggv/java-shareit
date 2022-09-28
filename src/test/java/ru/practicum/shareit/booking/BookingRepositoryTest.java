package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private User owner;
    private User booker;
    private Booking booking;
    private Item item;
    private final LocalDateTime start = LocalDateTime.parse("2100-09-01T01:00");
    private final LocalDateTime end = LocalDateTime.parse("2110-09-01T01:00");

    @BeforeEach
    void create() {
        owner = new User("owner", "owner@gmail.com");
        booker = new User("booker", "boker@gmail.com");
        item = new Item("item", "item description", true, owner, null);
        booking = new Booking(start, end, item, booker, Status.WAITING);
        owner = userRepository.save(owner);
        booker = userRepository.save(booker);
        item = itemRepository.save(item);
    }

    @Test
    void findBookingByBookerId() {
        booking = bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findBookingByBookerId(booker.getId(), PageRequest.of(0, 2));
        assertThat(bookings).hasSize(1).contains(booking);
    }

    @Test
    void findBookingsCurrentForBooker() {
        booking.setStart(LocalDateTime.parse("1900-09-01T01:00"));
        booking.setEnd(LocalDateTime.parse("3000-09-01T01:00"));
        booking = bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findBookingsCurrentForBooker(booker.getId(), LocalDateTime.now(),
                PageRequest.of(0, 2));
        assertThat(bookings).hasSize(1).contains(booking);
    }

    @Test
    void findBookingsPastForBooker() {
        booking.setStart(LocalDateTime.parse("1900-09-01T01:00"));
        booking.setEnd(LocalDateTime.parse("2000-09-01T01:00"));
        booking = bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findBookingsPastForBooker(booker.getId(), LocalDateTime.now(),
                PageRequest.of(0, 2));
        assertThat(bookings).hasSize(1).contains(booking);
    }

    @Test
    void findBookingsFutureForBooker() {
        booking.setStart(LocalDateTime.parse("2500-09-01T01:00"));
        booking.setEnd(LocalDateTime.parse("2600-09-01T01:00"));
        booking = bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findBookingsFutureForBooker(booker.getId(), LocalDateTime.now(),
                PageRequest.of(0, 2));
        assertThat(bookings).hasSize(1).contains(booking);
    }

    @Test
    void findBookingsByStatusAndBookerId() {
        booking = bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findBookingsByStatusAndBookerId(booker.getId(), Status.WAITING,
                PageRequest.of(0, 2));
        assertThat(bookings).hasSize(1).contains(booking);
    }

    @Test
    void findAllBookingsForItemOwner() {
        booking = bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findAllBookingsForItemOwner(owner.getId(),
                PageRequest.of(0, 2));
        assertThat(bookings).hasSize(1).contains(booking);
    }

    @Test
    void findBookingsCurrentForItemOwner() {
        booking.setStart(LocalDateTime.parse("1900-09-01T01:00"));
        booking.setEnd(LocalDateTime.parse("2600-09-01T01:00"));
        booking = bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findBookingsCurrentForItemOwner(owner.getId(), LocalDateTime.now(),
                PageRequest.of(0, 2));
        assertThat(bookings).hasSize(1).contains(booking);
    }

    @Test
    void findBookingsPastForItemOwner() {
        booking.setStart(LocalDateTime.parse("1900-09-01T01:00"));
        booking.setEnd(LocalDateTime.parse("2000-09-01T01:00"));
        booking = bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findBookingsPastForItemOwner(owner.getId(), LocalDateTime.now(),
                PageRequest.of(0, 2));
        assertThat(bookings).hasSize(1).contains(booking);
    }

    @Test
    void findBookingsFutureForItemOwner() {
        booking.setStart(LocalDateTime.parse("3000-09-01T01:00"));
        booking.setEnd(LocalDateTime.parse("3500-09-01T01:00"));
        booking = bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findBookingsFutureForItemOwner(owner.getId(), LocalDateTime.now(),
                PageRequest.of(0, 2));
        assertThat(bookings).hasSize(1).contains(booking);
    }

    @Test
    void findBookingsByStatusForItemOwner() {
        booking = bookingRepository.save(booking);

        List<Booking> bookings = bookingRepository.findBookingsByStatusForItemOwner(owner.getId(), Status.WAITING,
                PageRequest.of(0, 2));
        assertThat(bookings).hasSize(1).contains(booking);
    }

    @Test
    void isFree() {
        booking.setStart(LocalDateTime.parse("2000-09-01T01:00"));
        booking.setEnd(LocalDateTime.parse("2001-09-01T01:00"));
        bookingRepository.save(booking);
        boolean isBooked = bookingRepository.isFree(LocalDateTime.parse("2101-09-01T01:00"),
                LocalDateTime.parse("2102-09-01T01:00"),
                item.getId());
        assertTrue(isBooked);
    }

    @Test
    void findBookingByItemWithDateBefore() {
        booking.setStart(LocalDateTime.parse("1000-09-01T01:00"));
        booking.setEnd(LocalDateTime.parse("3500-09-01T01:00"));
        booking = bookingRepository.save(booking);

        assertEquals(booking, bookingRepository.findBookingByItemWithDateBefore(item.getId(), LocalDateTime.now()));
    }

    @Test
    void findBookingByItemWithDateAfter() {
        booking.setStart(LocalDateTime.parse("2500-09-01T01:00"));
        booking.setEnd(LocalDateTime.parse("3500-09-01T01:00"));
        booking = bookingRepository.save(booking);

        assertEquals(booking, bookingRepository.findBookingByItemWithDateAfter(item.getId(), LocalDateTime.now()));
    }

    @Test
    void isExists() {
        booking.setStart(LocalDateTime.parse("1000-09-01T01:00"));
        booking.setEnd(LocalDateTime.parse("1500-09-01T01:00"));
        bookingRepository.save(booking);
        boolean isExist = bookingRepository.isExists(item.getId(), booker.getId(), LocalDateTime.now());

        assertTrue(isExist);
    }
}