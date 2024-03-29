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
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    @EqualsAndHashCode.Exclude
    private long id;
    @Column(name = "user_name", nullable = false, length = 200)
    private String name;
    @Column(name = "user_email", nullable = false, length = 200)
    private String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
