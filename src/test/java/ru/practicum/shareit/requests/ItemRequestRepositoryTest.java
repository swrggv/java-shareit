package ru.practicum.shareit.requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRequestRepository requestRepository;
    @Autowired
    private UserRepository userRepository;

    private User requestor1;
    private User requestor2;
    private ItemRequest itemRequest;
    private ItemRequest itemRequest2;

    @BeforeEach
    void saveRequests() {
        requestor1 = new User("One", "one@gmail.com");
        requestor2 = new User("Two", "two@gmail.com");

        itemRequest = new ItemRequest("One", requestor1, LocalDateTime.now(), null);
        itemRequest2 = new ItemRequest("Two", requestor2, LocalDateTime.now(), null);
    }

    @Test
    void findAllByRequestor() {
        requestor1 = userRepository.save(requestor1);
        requestor2 = userRepository.save(requestor2);

        itemRequest = requestRepository.save(itemRequest);
        itemRequest2 = requestRepository.save(itemRequest2);

        List<ItemRequest> requests = requestRepository.findAllByRequestor(requestor1, Sort.by("created"));

        assertThat(requests).hasSize(1).contains(itemRequest);

    }

    @Test
    void findAllByRequestorNotLike() {
        requestor1 = userRepository.save(requestor1);
        requestor2 = userRepository.save(requestor2);

        itemRequest = requestRepository.save(itemRequest);
        itemRequest2 = requestRepository.save(itemRequest2);
        List<ItemRequest> requests = requestRepository
                .findAllByRequestorNotLike(requestor1.getId(), PageRequest.of(0, 2));

        assertThat(requests).hasSize(1).contains(itemRequest2);
    }
}