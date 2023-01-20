package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ModelNotFoundException;
import ru.practicum.shareit.requests.dto.RequestDto;
import ru.practicum.shareit.requests.dto.RequestMapper;
import ru.practicum.shareit.requests.model.Request;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    private final RequestMapper mapper;

    @Transactional
    @Override
    public RequestDto addRequest(RequestDto requestDto, long requestorId) {
        User requestor = fromOptionalToUser(requestorId);
        Request request = mapper.toRequest(requestDto, requestor);
        return mapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    public List<RequestDto> getAllRequestsForRequestor(long requestorId) {
        User requestor = fromOptionalToUser(requestorId);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        return mapper.toRequestDtoList(requestRepository.findAllByRequestor(requestor, sort));
    }

    @Override
    public List<RequestDto> getAllRequests(long requestorId, int from, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        int page = getPageNumber(from, size);
        return mapper
                .toRequestDtoList(requestRepository
                        .findAllByRequestorNotLike(requestorId, PageRequest.of(page, size, sort)));
    }

    @Override
    public List<RequestDto> getAllRequests() {
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        return mapper.toRequestDtoList(requestRepository.findAll(sort));
    }

    @Override
    public RequestDto getOneRequest(long requestId, long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ModelNotFoundException(String.format("User %d not found", userId));
        }
        Request request = fromOptionalToRequest(requestId);
        return mapper.toRequestDto(request);
    }

    private User fromOptionalToUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ModelNotFoundException(String.format("User %d not found", userId)));
    }

    private int getPageNumber(int from, int size) {
        return from / size;
    }

    private Request fromOptionalToRequest(long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new ModelNotFoundException(String.format("Request %d not found", requestId)));
    }
}
