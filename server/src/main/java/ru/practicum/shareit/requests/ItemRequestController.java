package ru.practicum.shareit.requests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") long requestorId,
                                     @RequestBody ItemRequestDto itemRequestDto) {
        ItemRequestDto result = itemRequestService.addRequest(itemRequestDto, requestorId);
        log.info("Request {} was added", result);
        return result;
    }

    @GetMapping
    public List<ItemRequestDto> getAllRequestsForRequestor(@RequestHeader("X-Sharer-User-Id") long requestorId) {
        List<ItemRequestDto> result = itemRequestService.getAllRequestsForRequestor(requestorId);
        log.info("Get all requests for requestor {}", requestorId);
        return result;
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestParam(value = "from", required = false, defaultValue = "0")
                                               @Min(0) int from,
                                               @RequestParam(value = "size", required = false, defaultValue = "20")
                                               @Min(1) int size,
                                               @RequestHeader("X-Sharer-User-Id") long requestorId) {
        List<ItemRequestDto> result = itemRequestService.getAllRequests(requestorId, from, size);
        log.info("Get all requests");
        return result;
    }

    @GetMapping("{requestId}")
    public ItemRequestDto getOneRequest(@PathVariable long requestId,
                                        @RequestHeader("X-Sharer-User-Id") long userId) {
        ItemRequestDto result = itemRequestService.getOneRequest(requestId, userId);
        log.info("Get request {}", requestId);
        return result;
    }
}
