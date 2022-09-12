package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto addRequest(@RequestHeader("X-Sharer-User-Id") long requestorId,
                                     @Validated(Create.class) @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.addRequest(itemRequestDto, requestorId);
    }


    //Запросы должны возвращаться в отсортированном порядке от более новых к более старым.
    @GetMapping
    public List<ItemRequestDto> getAllRequestsForRequestor(@RequestHeader("X-Sharer-User-Id") long requestorId) {
        return itemRequestService.getAllRequestsForRequestor(requestorId);
    }

    // Результаты должны возвращаться постранично.
    // Для этого нужно передать два параметра: from — индекс первого элемента,
    // начиная с 0, и size — количество элементов для отображения.
    // хэдэре передается id юзера, типо выводить все кроме ЕГО запросов?

    //может быть проблема с size?
    //нужно добавить айдишку которая приходит в хэдере?
    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestParam(value = "from", required = false, defaultValue = "0") @Min(0) int from,
                                               @RequestParam(value = "size", required = false, defaultValue = "20") int size,
                                               @RequestHeader("X-Sharer-User-Id") long requestorId) {
        return itemRequestService.getAllRequests(requestorId, from, size);
    }

    //смотреть может любой пользователь
    @GetMapping("{requestId}")
    public ItemRequestDto getOneRequest(@PathVariable long requestId,
                                        @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getOneRequest(requestId, userId);
    }
}
