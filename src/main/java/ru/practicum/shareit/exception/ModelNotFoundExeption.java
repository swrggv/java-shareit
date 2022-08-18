package ru.practicum.shareit.exception;

public class ModelNotFoundExeption extends RuntimeException {
    public ModelNotFoundExeption(String message) {
        super(message);
    }
}
