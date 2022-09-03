package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOutcome;

import java.util.List;

public interface BookingService {
    BookingDtoOutcome addBooking(BookingDto bookingDto, long userId);

    BookingDtoOutcome approveBooking(long bookingId, Boolean approved, long userId);

    BookingDtoOutcome getBookingByIdIfOwnerOrBooker(long bookingId, long userId);

    List<BookingDtoOutcome> getBookingByUserSorted(long userId, State state);

    List<BookingDtoOutcome> getBookingByItemOwner(long userId, State state);
}
