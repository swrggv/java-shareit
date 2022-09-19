package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.exception.ModelNotFoundException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestPropertySource(properties = { "db.name=test"})
class ItemRequestServiceImplTest {
    @Autowired
    private final ItemRequestService itemRequestService;
    @Autowired
    private final UserService userService;
    private ItemRequestDto itemRequestDto;
    private UserDto requestorDto;

    @BeforeEach
    void prepare() {
        requestorDto = new UserDto("requestor", "requestor@gmail.com");
        itemRequestDto = new ItemRequestDto("request description",
                LocalDateTime.parse("2100-09-01T01:00"), 1L, new ArrayList<>());
    }

    @Test
    void addRequest() {
        requestorDto = userService.addUser(requestorDto);
        itemRequestDto = itemRequestService.addRequest(itemRequestDto, requestorDto.getId());

        assertEquals(itemRequestDto, itemRequestService.getOneRequest(itemRequestDto.getId(), requestorDto.getId()));
    }

    @Test
    void getAllRequestsForRequestor() {
        requestorDto = userService.addUser(requestorDto);
        itemRequestDto = itemRequestService.addRequest(itemRequestDto, requestorDto.getId());
        assertThat(itemRequestService.getAllRequestsForRequestor(requestorDto.getId())).hasSize(1).contains(itemRequestDto);
    }

    @Test
    void getAllRequests() {
        requestorDto = userService.addUser(requestorDto);
        itemRequestDto = itemRequestService.addRequest(itemRequestDto, requestorDto.getId());
        List<ItemRequestDto> requests = itemRequestService.getAllRequests(requestorDto.getId(), 0, 2);
        assertThat(requests).hasSize(0);
    }

    @Test
    void testGetAllRequests() {
        requestorDto = userService.addUser(requestorDto);
        itemRequestDto = itemRequestService.addRequest(itemRequestDto, requestorDto.getId());
        List<ItemRequestDto> requests = itemRequestService.getAllRequests();
        assertThat(requests).hasSize(1).contains(itemRequestDto);
    }

    @Test
    void getOneRequest() {
        requestorDto = userService.addUser(requestorDto);
        itemRequestDto = itemRequestService.addRequest(itemRequestDto, requestorDto.getId());
        assertThat(itemRequestDto.getId()).isNotZero();
        assertThat(itemRequestDto.getDescription()).isEqualTo("request description");
        assertThat(itemRequestDto.getItems().size()).isZero();
        assertThat(itemRequestDto.getRequestorId()).isEqualTo(requestorDto.getId());
    }

    @Test
    void getOneRequest_UserNotFound() {
        ModelNotFoundException ex = assertThrows(ModelNotFoundException.class,
                () -> itemRequestService.getOneRequest(requestorDto.getId(), -1));
        assertThat(ex.getMessage()).contains(String.format("User %d not found", -1));
    }
}