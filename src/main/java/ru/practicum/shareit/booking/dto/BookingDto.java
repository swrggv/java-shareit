package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

    private long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private ItemBooking item;

    private Booker booker;

    private Status status;

    @Data
    public static class Booker {
        private final long id;
        private final String name;
    }

    @Data
    public static class ItemBooking {
        private final long id;
        private final String name;
    }
}
