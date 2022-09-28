package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ViolationTest {
    private Violation ex;

    @Test
    void getError() {
        ex = new Violation("Error");
        assertThat(ex.getError()).contains("Error");
    }
}