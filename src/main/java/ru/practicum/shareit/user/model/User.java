package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;


@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "users")
@ToString
@EqualsAndHashCode
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    @EqualsAndHashCode.Exclude
    private long id;

    @Column(name = "user_name")
    private String name;

    @Column(name = "user_email")
    private String email;

    //это видимо удалить, не помню зачем пихнула
    /*@OneToMany(mappedBy = "owner")
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Item> items;*/

    public User() {
    }
}
