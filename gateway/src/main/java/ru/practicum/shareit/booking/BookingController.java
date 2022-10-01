package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/bookings")
@Validated
@Slf4j
public class BookingController {
    private final BookingClient bookingClient;

    public BookingController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @PostMapping
    public ResponseEntity<Object> addBooking(@Validated(Create.class) @RequestBody BookItemRequestDto bookItemRequestDto,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Booking was created by user {}", userId);
        return bookingClient.addBooking(bookItemRequestDto, userId);
    }

    @PatchMapping("{bookingId}")
    public ResponseEntity<Object> approveBooking(@PathVariable long bookingId,
                                                 @RequestParam Boolean approved,
                                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Booking {} was approved", bookingId);
        return bookingClient.approveBooking(bookingId, approved, userId);
    }

    @GetMapping("{bookingId}")
    @Cacheable("getBooking")
    public ResponseEntity<Object> getBooking(@PathVariable long bookingId,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get booking {}", bookingId);
        return bookingClient.getBookingByIdIfOwnerOrBooker(bookingId, userId);
    }

    @GetMapping()
    @Cacheable("getBookingByUserSorted")
    public ResponseEntity<Object> getBookingByUserSorted(
            @RequestParam(name = "state", required = false, defaultValue = "ALL") State state,
            @RequestHeader("X-Sharer-User-Id") long bookerId,
            @RequestParam(value = "from", required = false, defaultValue = "0") @Min(0) int from,
            @RequestParam(value = "size", required = false, defaultValue = "20") @Min(1) int size) {
        log.info("Get all bookings for booker {}", bookerId);
        return bookingClient.getBookingByUserSorted(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    @Cacheable("getBookingsForItemOwner")
    public ResponseEntity<Object> getBookingsForItemOwner(
            @RequestParam(name = "state", required = false, defaultValue = "ALL") State state,
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(value = "from", required = false, defaultValue = "0") @Min(0) int from,
            @RequestParam(value = "size", required = false, defaultValue = "20") @Min(1) int size) {
        log.info("Get all bookings for item owner {}", ownerId);
        return bookingClient.getBookingByItemOwner(ownerId, state, from, size);
    }
}
