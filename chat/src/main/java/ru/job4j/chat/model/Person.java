package ru.job4j.chat.model;

import lombok.Getter;
import lombok.Setter;
import ru.job4j.chat.handlers.Operation;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Objects;

/**
 * Model of person
 */
@Entity
@Table(name = "persons")
@Getter
@Setter
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull(message = "Id must by non null", groups = {
            Operation.OnUpdate.class, Operation.OnDelete.class
    })
    @Null(groups = Operation.OnCreate.class)
    private int id;

    @NotBlank(message = "Name must be non empty", groups = {
            Operation.OnCreate.class
    })
    private String username;

    @NotBlank(message = "Password must be non empty", groups = {
            Operation.OnCreate.class, Operation.OnUpdate.class
    })
    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    public static Person of(String username, String password, Role role) {
        Person person = new Person();
        person.username = username;
        person.password = password;
        person.role = role;
        return person;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Person person = (Person) o;
        return id == person.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
