package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static Booking toBooking(BookingDto bookingDto, Item item, User booker) {
        Booking booking = new Booking();
        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(bookingDto.getStatus());
        return booking;
    }

    public static BookingDto toBookingDtoIncome(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItemId(booking.getItem().getId());
        bookingDto.setBookerId(booking.getBooker().getId());
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }

    public static BookingDtoOutcome toBookingDtoOutcome(Booking booking) {
        BookingDtoOutcome bookingDtoOutcome = new BookingDtoOutcome();
        bookingDtoOutcome.setId(booking.getId());
        bookingDtoOutcome.setStart(booking.getStart());
        bookingDtoOutcome.setEnd(booking.getEnd());
        bookingDtoOutcome.setItem(booking.getItem());
        bookingDtoOutcome.setBooker(booking.getBooker());
        bookingDtoOutcome.setStatus(booking.getStatus());
        return bookingDtoOutcome;
    }

    public static BookingDtoForItemOwner toBookingDtoForItemOwner(Booking booking) {
        BookingDtoForItemOwner bookingDtoWithoutDate = new BookingDtoForItemOwner();
        bookingDtoWithoutDate.setId(booking.getId());
        bookingDtoWithoutDate.setBookerId(booking.getBooker().getId());
        bookingDtoWithoutDate.setStart(booking.getStart());
        bookingDtoWithoutDate.setEnd(booking.getEnd());
        bookingDtoWithoutDate.setStatus(booking.getStatus());
        return bookingDtoWithoutDate;
    }
}
