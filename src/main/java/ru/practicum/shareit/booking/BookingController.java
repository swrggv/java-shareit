package ru.practicum.shareit.booking;

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
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto addBooking(@Validated(Create.class) @RequestBody BookItemRequestDto bookItemRequestDto,
                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.addBooking(bookItemRequestDto, userId);
    }

    @PatchMapping("{bookingId}")
    public BookingDto approveBooking(@PathVariable long bookingId,
                                     @RequestParam Boolean approved,
                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.approveBooking(bookingId, approved, userId);
    }

    @GetMapping("{bookingId}")
    public BookingDto getBooking(@PathVariable long bookingId,
                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getBookingByIdIfOwnerOrBooker(bookingId, userId);
    }

    @GetMapping()
    public List<BookingDto> getBookingByUserSorted(
            @RequestParam(name = "state", required = false, defaultValue = "ALL") State state,
            @RequestHeader("X-Sharer-User-Id") long bookerId,
            @RequestParam(value = "from", required = false, defaultValue = "0") @Min(0) int from,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size) {
        return bookingService.getBookingByUserSorted(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsForItemOwner(
            @RequestParam(name = "state", required = false, defaultValue = "ALL") State state,
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(value = "from", required = false, defaultValue = "0") @Min(0) int from,
            @RequestParam(value = "size", required = false, defaultValue = "20") int size) {
        return bookingService.getBookingByItemOwner(ownerId, state, from, size);
    }


}
