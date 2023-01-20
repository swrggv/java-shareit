package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDtoWithDate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(target = "id", ignore = true)
    Booking toBooking(BookItemRequestDto bookItemRequestDto, Item item, User booker);

    @Mapping(source = "booking.item.id", target = "item.id")
    @Mapping(source = "booking.item.name", target = "item.name")
    @Mapping(source = "booking.booker.id", target = "booker.id")
    @Mapping(source = "booking.booker.name", target = "booker.name")
    BookingDto toBookingDto(Booking booking);

    List<BookingDto> toListBookingDto(List<Booking> bookings);

    @Mapping(source = "booking.booker.id", target = "bookerId")
    ItemDtoWithDate.BookingDto toItemDtoWithDateToBookingDto(Booking booking);
}
