package ru.practicum.shareit.requests;

import org.springframework.data.domain.Sort;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addRequest(ItemRequestDto itemRequestDto, long requestorId);

    List<ItemRequestDto> getAllRequestsForRequestor(long requestorId);

    List<ItemRequestDto> getAllRequests(long requestorId, int from, int size);

    List<ItemRequestDto> getAllRequests();

    ItemRequestDto getOneRequest(long requestId, long userId);
}
