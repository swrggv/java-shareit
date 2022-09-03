package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwner(User owner);

    @Query("select i from Item i " +
            "where (lower(i.name) like concat('%', lower(:text),'%') " +
            "or lower(i.description) like concat('%', lower(:text), '%')) " +
            "and i.available = true")
    List<Item> findByNameOrDescription(@Param("text") String text);
}
