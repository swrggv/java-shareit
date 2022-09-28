package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ModelNotFoundException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.dto.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public ItemRequestDto addRequest(ItemRequestDto itemRequestDto, long requestorId) {
        User requestor = fromOptionalToUser(requestorId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, requestor);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getAllRequestsForRequestor(long requestorId) {
        User requestor = fromOptionalToUser(requestorId);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        return ItemRequestMapper.toItemRequestDtoList(itemRequestRepository.findAllByRequestor(requestor, sort));
    }

    @Override
    public List<ItemRequestDto> getAllRequests(long requestorId, int from, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        int page = getPageNumber(from, size);
        return ItemRequestMapper
                .toItemRequestDtoList(itemRequestRepository
                        .findAllByRequestorNotLike(requestorId, PageRequest.of(page, size, sort)));
    }

    @Override
    public List<ItemRequestDto> getAllRequests() {
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        return ItemRequestMapper.toItemRequestDtoList(itemRequestRepository.findAll(sort));
    }

    @Override
    public ItemRequestDto getOneRequest(long requestId, long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ModelNotFoundException(String.format("User %d not found", userId));
        }
        ItemRequest itemRequest = fromOptionalToRequest(requestId);
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    private User fromOptionalToUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ModelNotFoundException(String.format("User %d not found", userId)));
    }

    private int getPageNumber(int from, int size) {
        return from / size;
    }

    private ItemRequest fromOptionalToRequest(long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ModelNotFoundException(String.format("Request %d not found", requestId)));
    }
}
