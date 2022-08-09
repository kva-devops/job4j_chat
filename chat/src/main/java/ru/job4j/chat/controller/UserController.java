package ru.job4j.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.job4j.chat.handlers.Operation;
import ru.job4j.chat.model.Person;
import ru.job4j.chat.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * REST controller for working with objects of Person
 */
@RestController
@Validated
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    /**
     * Business logic for working with object of Person
     */
    private final UserService userService;

    /**
     * GET method for getting all objects of Person
     * @return List of Persons
     */
    @GetMapping("/all")
    public List<Person> findAll() {
        return userService.findAllPerson();
    }

    /**
     * GET method for getting object of Person by person ID
     * @param id - person ID
     * @return found object of Person
     */
    @GetMapping("/{id}")
    public Person findById(@PathVariable int id) {
        return userService.findPersonById(id);
    }

    /**
     * GET method for getting object of Person by username of person
     * @param username - username of person
     * @return found object of Person
     */
    @GetMapping("/username/{username}")
    public Person findByUsername(@PathVariable String username) {
        return userService.findPersonByUsername(username);
    }

    /**
     * POST method for signing up new person
     * @param id - role ID
     * @param person - object of Person
     * @param token - JWT token
     * @return signed up object of Person
     */
    @PostMapping("/sign-up/role/{id}")
    @Validated(Operation.OnCreate.class)
    public Person signUp(
            @PathVariable("id") int id,
            @Valid @RequestBody Person person,
            @RequestHeader("Authorization") String token) {
        return userService.singUpPerson(id, person, token);
    }

    /**
     * PATCH method for updating object of Person
     * @param person - object of Person
     */
    @PatchMapping("/")
    @Validated(Operation.OnUpdate.class)
    public void update(@Valid @RequestBody Person person) {
        userService.updatePerson(person);
    }

    /**
     * DELETE method for deleting object of Person by person ID
     * @param id - person ID
     */
    @DeleteMapping("/{id}")
    @Validated(Operation.OnDelete.class)
    public void delete(@Valid @PathVariable int id) {
        userService.deletePersonById(id);
    }
}
