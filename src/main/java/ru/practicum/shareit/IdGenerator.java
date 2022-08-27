package ru.practicum.shareit;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class IdGenerator {
    private long id = 0;

    public IdGenerator() {
    }

    public long getId() {
        return ++id;
    }
}
