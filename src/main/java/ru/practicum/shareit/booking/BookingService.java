package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(BookItemRequestDto bookItemRequestDto, long userId);

    BookingDto approveBooking(long bookingId, Boolean approved, long userId);

    BookingDto getBookingByIdIfOwnerOrBooker(long bookingId, long userId);

    List<BookingDto> getBookingByUserSorted(long userId, State state);

    List<BookingDto> getBookingByItemOwner(long userId, State state);
}
