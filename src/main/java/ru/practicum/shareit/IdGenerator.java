package ru.practicum.shareit;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class IdGenerator {
    private long id = 0;

    public IdGenerator() {
    }

    public long getId() {
        return ++id;
    }
}
