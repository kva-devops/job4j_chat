package ru.job4j.chat.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.chat.model.Person;

import java.util.Optional;

public interface PersonRepository extends CrudRepository<Person, Integer> {

    Optional<Person> findByUsername(String username);
}
