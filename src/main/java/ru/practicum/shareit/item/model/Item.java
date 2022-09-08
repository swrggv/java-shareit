package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Entity
@Table(name = "items")
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    @EqualsAndHashCode.Exclude
    private long id;

    @Column(name = "item_name")
    private String name;

    @Column(name = "item_description")
    private String description;

    @Column(name = "is_available")
    private Boolean available;


    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "user_id")
    @EqualsAndHashCode.Exclude
    private User owner;

    @Transient
    private ItemRequest itemRequest;

    public Item() {
    }
}
