package ru.practicum.shareit.requests;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    //не хнаю что с ним делать
    List<ItemRequest> findItemRequestByRequestor(User requestor);

    List<ItemRequest> findAllByRequestor(User requestor, Sort sort);

    @Query(value = "from ItemRequest where requestor.id <> :requestorId")
    List<ItemRequest> findAllByRequestorNotLike(@Param("requestorId") long requestor, Pageable pageable);
}
