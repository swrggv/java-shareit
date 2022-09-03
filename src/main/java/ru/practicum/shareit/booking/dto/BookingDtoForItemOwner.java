package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDtoForItemOwner {

    private long id;

    private long bookerId;

    private LocalDateTime start;

    private LocalDateTime end;

    private Status status;

}
