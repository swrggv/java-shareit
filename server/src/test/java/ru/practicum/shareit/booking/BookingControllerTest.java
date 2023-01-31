package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private BookingDto outputDto;
    private BookItemRequestDto inputDto;
    private final LocalDateTime START = LocalDateTime.now().plusDays(1);
    private static final LocalDateTime END = LocalDateTime.now().plusDays(2);
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private static final long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        inputDto = BookItemRequestDto.builder()
                .itemId(1L)
                .start(START)
                .end(END)
                .build();
        outputDto = BookingDto.builder()
                .start(START)
                .end(END)
                .item(new BookingDto.ItemBooking(1L, "item"))
                .build();
    }

    @Test
    void addBooking() throws Exception {
        Mockito.when(bookingService.addBooking(any(), anyLong()))
                .thenReturn(outputDto);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(inputDto))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start", startsWith(START.format(FORMAT)), String.class))
                .andExpect(jsonPath("$.end", startsWith(END.format(FORMAT)), String.class));
    }

    @Test
    void approveBooking() throws Exception {
        Mockito.when(bookingService.approveBooking(anyLong(), any(), anyLong()))
                .thenReturn(outputDto);
        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", USER_ID)
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start", startsWith(START.format(FORMAT)), String.class))
                .andExpect(jsonPath("$.end", startsWith(END.format(FORMAT)), String.class));
    }

    @Test
    void getBooking() throws Exception {
        Mockito.when(bookingService.getBookingByIdIfOwnerOrBooker(anyLong(), anyLong()))
                .thenReturn(outputDto);
        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start", startsWith(START.format(FORMAT)), String.class))
                .andExpect(jsonPath("$.end", startsWith(END.format(FORMAT)), String.class));
    }

    @Test
    void getBookingByUserSorted() throws Exception {
        Mockito.when(bookingService.getBookingByUserSorted(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(outputDto));
        mvc.perform(get("/bookings")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].start", startsWith(START.format(FORMAT)), String.class))
                .andExpect(jsonPath("$[0].end", startsWith(END.format(FORMAT)), String.class));
    }

    @Test
    void getBookingsForItemOwner() throws Exception {
        Mockito.when(bookingService.getBookingByItemOwner(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(outputDto));
        mvc.perform(get("/bookings/owner")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].start", startsWith(START.format(FORMAT)), String.class))
                .andExpect(jsonPath("$[0].end", startsWith(END.format(FORMAT)), String.class));
    }
}