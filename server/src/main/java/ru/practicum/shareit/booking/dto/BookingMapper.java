package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDtoWithDate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {

    public static Booking toBooking(BookItemRequestDto bookItemRequestDto, Item item, User booker) {
        Booking booking = new Booking();
        booking.setStart(bookItemRequestDto.getStart());
        booking.setEnd(bookItemRequestDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        return booking;
    }

    public static BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setBooker(new BookingDto.Booker(booking.getBooker().getId(), booking.getBooker().getName()));
        bookingDto.setItem(new BookingDto.ItemBooking(booking.getItem().getId(), booking.getItem().getName()));
        bookingDto.setStatus(booking.getStatus());
        return bookingDto;
    }

    public static List<BookingDto> toListBookingDto(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    public static ItemDtoWithDate.BookingDto toItemDtoWithDateToBookingDto(Booking booking) {
        return new ItemDtoWithDate.BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getBooker().getId());
    }
}
