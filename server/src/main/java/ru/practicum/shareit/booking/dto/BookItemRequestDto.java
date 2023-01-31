package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookItemRequestDto {
    @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
    @FutureOrPresent(message = "Start date can not be in past")
    private LocalDateTime start;

    @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
    @Future(message = "End date can not be in past")
    private LocalDateTime end;

    private Long itemId;
}
