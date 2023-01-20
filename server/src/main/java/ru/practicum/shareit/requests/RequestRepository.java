package ru.practicum.shareit.requests;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.requests.model.Request;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findItemRequestByRequestor(User requestor);

    List<Request> findAllByRequestor(User requestor, Sort sort);

    @Query(value = "from Request where requestor.id <> :requestorId")
    List<Request> findAllByRequestorNotLike(@Param("requestorId") long requestor, Pageable pageable);
}
