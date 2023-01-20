package ru.practicum.shareit.requests;

import ru.practicum.shareit.requests.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto addRequest(RequestDto requestDto, long requestorId);

    List<RequestDto> getAllRequestsForRequestor(long requestorId);

    List<RequestDto> getAllRequests(long requestorId, int from, int size);

    List<RequestDto> getAllRequests();

    RequestDto getOneRequest(long requestId, long userId);
}
