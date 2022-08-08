package ru.job4j.chat.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.chat.model.Person;

import java.util.Optional;

/**
 * DAO interface for Persons
 */
public interface PersonRepository extends CrudRepository<Person, Integer> {

    /**
     * Getting Person object by username of person
     * @param username username of person
     * @return Person object (wrapped to optional)
     */
    Optional<Person> findByUsername(String username);
}
