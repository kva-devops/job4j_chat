package ru.job4j.chat.model;

import ru.job4j.chat.handlers.Operation;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Objects;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull(message = "Id must by non null", groups = {
            Operation.OnUpdate.class, Operation.OnDelete.class
    })
    @Null(groups = Operation.OnCreate.class)
    private int id;

    @NotBlank(message = "Name must be non empty", groups = {
            Operation.OnCreate.class, Operation.OnUpdate.class
    })
    private String name;

    public static Role of(String name) {
        Role role = new Role();
        role.name = name;
        return role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Role role = (Role) o;
        return id == role.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
