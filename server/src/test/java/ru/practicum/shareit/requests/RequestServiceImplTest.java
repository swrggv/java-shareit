package ru.practicum.shareit.requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.requests.dto.RequestDto;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = "application.properties")
class RequestServiceImplTest {

    @Autowired
    private RequestService requestService;

    @Autowired
    private UserRepository userRepository;

    private RequestDto requestDto;
    private User user;
    private User anotherUser;

    @BeforeEach
    void setUp() {
        requestDto = RequestDto.builder()
                .id(1L)
                .description("request")
                .requestorId(1L)
                .items(List.of())
                .build();
        user = User.builder()
                .id(1L)
                .name("user")
                .email("user@gmail.com")
                .build();
        anotherUser = User.builder()
                .id(2L)
                .name("another user")
                .email("another@gmail.com")
                .build();
        userRepository.save(user);
        userRepository.save(anotherUser);
    }

    @Test
    void addRequest() {
        RequestDto result = requestService.addRequest(requestDto, user.getId());
        assertThat(result, equalTo(requestDto));
    }

    @Test
    void getAllRequestsForRequestor() {
        requestService.addRequest(requestDto, user.getId());
        List<RequestDto> result = requestService.getAllRequestsForRequestor(user.getId());
        assertThat(result, equalTo(List.of(requestDto)));
    }

    @Test
    void getAllRequests() {
        requestService.addRequest(requestDto, user.getId());
        List<RequestDto> result = requestService.getAllRequests(anotherUser.getId(), 0, 10);
        assertThat(result, equalTo(List.of(requestDto)));
    }

    @Test
    void getOneRequest() {
        requestService.addRequest(requestDto, user.getId());
        RequestDto result = requestService.getOneRequest(requestDto.getId(), user.getId());
        assertThat(result, equalTo(requestDto));
    }

    @Test
    void getOneRequestWithModelNotFoundException() {
        requestService.addRequest(requestDto, user.getId());
        assertThatThrownBy(() -> requestService.getOneRequest(requestDto.getId(), 1000));
    }
}