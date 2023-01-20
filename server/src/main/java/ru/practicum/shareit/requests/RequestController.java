package ru.practicum.shareit.requests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.RequestDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
public class RequestController {
    private final RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public RequestDto addRequest(@RequestHeader("X-Sharer-User-Id") long requestorId,
                                 @Valid @RequestBody RequestDto requestDto) {
        RequestDto result = requestService.addRequest(requestDto, requestorId);
        log.info("Request {} was added", result);
        return result;
    }

    @GetMapping
    public List<RequestDto> getAllRequestsForRequestor(@RequestHeader("X-Sharer-User-Id") long requestorId) {
        List<RequestDto> result = requestService.getAllRequestsForRequestor(requestorId);
        log.info("Get all requests for requestor {}", requestorId);
        return result;
    }

    @GetMapping("/all")
    public List<RequestDto> getAllRequests(@RequestParam(value = "from", required = false, defaultValue = "0")
                                               int from,
                                           @RequestParam(value = "size", required = false, defaultValue = "20")
                                               int size,
                                           @RequestHeader("X-Sharer-User-Id") long requestorId) {
        List<RequestDto> result = requestService.getAllRequests(requestorId, from, size);
        log.info("Get all requests");
        return result;
    }

    @GetMapping("{requestId}")
    public RequestDto getOneRequest(@PathVariable long requestId,
                                    @RequestHeader("X-Sharer-User-Id") long userId) {
        RequestDto result = requestService.getOneRequest(requestId, userId);
        log.info("Get request {}", requestId);
        return result;
    }
}
