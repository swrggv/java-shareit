package ru.practicum.shareit.requests.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "requests")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    @EqualsAndHashCode.Exclude
    private long id;
    @Column(name = "request_description")
    private String description;
    @ManyToOne
    @JoinColumn(name = "requestor_id", referencedColumnName = "user_id")
    private User requestor;
    @Column(name = "created")
    @DateTimeFormat(pattern = "yyyy-MM-ddTHH:mm:ss")
    private LocalDateTime created;
    @OneToMany(mappedBy = "itemRequest")
    private List<Item> items = new ArrayList<>();

    public ItemRequest(String description, User requestor, LocalDateTime created, List<Item> items) {
        this.description = description;
        this.requestor = requestor;
        this.created = created;
        this.items = items;
    }
}
