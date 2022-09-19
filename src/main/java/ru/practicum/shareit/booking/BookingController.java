package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Validated
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto addBooking(@Validated(Create.class) @RequestBody BookItemRequestDto bookItemRequestDto,
                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        BookingDto result = bookingService.addBooking(bookItemRequestDto, userId);
        log.info("Booking {} was created", result);
        return result;
    }

    @PatchMapping("{bookingId}")
    public BookingDto approveBooking(@PathVariable long bookingId,
                                     @RequestParam Boolean approved,
                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        BookingDto result = bookingService.approveBooking(bookingId, approved, userId);
        log.info("Booking {} was approved", bookingId);
        return result;
    }

    @GetMapping("{bookingId}")
    public BookingDto getBooking(@PathVariable long bookingId,
                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        BookingDto result = bookingService.getBookingByIdIfOwnerOrBooker(bookingId, userId);
        log.info("Get booking {}", result);
        return result;
    }

    @GetMapping()
    public List<BookingDto> getBookingByUserSorted(
            @RequestParam(name = "state", required = false, defaultValue = "ALL") State state,
            @RequestHeader("X-Sharer-User-Id") long bookerId,
            @RequestParam(value = "from", required = false, defaultValue = "0") @Min(0) int from,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size) {
        List<BookingDto> result = bookingService.getBookingByUserSorted(bookerId, state, from, size);
        log.info("Get all bookings for booker {}", bookerId);
        return result;
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsForItemOwner(
            @RequestParam(name = "state", required = false, defaultValue = "ALL") State state,
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(value = "from", required = false, defaultValue = "0") @Min(0) int from,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size) {
        List<BookingDto> result = bookingService.getBookingByItemOwner(ownerId, state, from, size);
        log.info("Get all bookings for item owner {}", ownerId);
        return result;
    }
}
