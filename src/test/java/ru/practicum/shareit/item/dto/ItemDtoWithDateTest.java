package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoWithDateTest {
    @Autowired
    private JacksonTester<ItemDtoWithDate> json;

    @Test
    void convert() throws IOException {
        ItemDtoWithDate itemDtoWithDate = new ItemDtoWithDate(1,
                "item",
                "description",
                true,
                1L,
                new ItemDtoWithDate.BookingDto(1L,
                        LocalDateTime.parse("2100-09-01T01:00"),
                        LocalDateTime.parse("2100-09-01T01:00"),
                        1L),
                new ItemDtoWithDate.BookingDto(1L,
                        LocalDateTime.parse("2100-09-01T01:00"),
                        LocalDateTime.parse("2100-09-01T01:00"),
                        1L),
                Collections.EMPTY_LIST);
        var result = json.write(itemDtoWithDate);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.requestId");
        assertThat(result).hasJsonPath("$.lastBooking");
        assertThat(result).hasJsonPath("$.nextBooking");
        assertThat(result).hasJsonPath("$.comments");
        assertThat(result).extractingJsonPathValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathValue("$.requestId").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.lastBooking.start")
                .isEqualTo("2100-09-01T01:00:00");
        assertThat(result).extractingJsonPathValue("$.requestId").isEqualTo(1);
    }
}