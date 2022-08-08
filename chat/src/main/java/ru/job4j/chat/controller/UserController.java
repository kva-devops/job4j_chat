package ru.job4j.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.chat.handlers.Operation;
import ru.job4j.chat.model.Person;
import ru.job4j.chat.model.Role;
import ru.job4j.chat.repository.PersonRepository;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * REST controller for working with objects of Person
 */
@RestController
@Validated
@RequestMapping("/users")
public class UserController {

    @Autowired
    private RestTemplate rest;

    private BCryptPasswordEncoder encoder;

    /**
     * DAO for objects of Person
     */
    private final PersonRepository personRepository;

    /**
     * URI for getting object of Role by role ID
     */
    private static final String ROLE_API_ID = "http://localhost:8080/role/{id}";

    public UserController(PersonRepository personRepository,
                          BCryptPasswordEncoder encoder) {
        this.personRepository = personRepository;
        this.encoder = encoder;
    }

    /**
     * GET method for getting all objects of Person
     * @return List of Persons
     */
    @GetMapping("/all")
    public List<Person> findAll() {
        return StreamSupport.stream(
                this.personRepository.findAll().spliterator(), false
        ).collect(Collectors.toList());
    }

    /**
     * GET method for getting object of Person by person ID
     * @param id - person ID
     * @return found object of Person
     */
    @GetMapping("/{id}")
    public Person findById(@PathVariable int id) {
        var person = this.personRepository.findById(id);
        if (person.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found");
        }
        return person.get();
    }

    /**
     * GET method for getting object of Person by username of person
     * @param username - username of person
     * @return found object of Person
     */
    @GetMapping("/username/{username}")
    public Person findByUsername(@PathVariable String username) {
        var person = this.personRepository.findByUsername(username);
        // add validation
        if (person.isEmpty()) {
            //add validation
            throw new RuntimeException();
        }
        return person.get();
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
        if (person.getUsername() == null || person.getPassword() == null) {
            throw new NullPointerException("Username or Password field's is empty");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<Role> role = rest.exchange(ROLE_API_ID, HttpMethod.GET, entity, Role.class, id);
        Person buff = Person.of(
                person.getUsername(),
                encoder.encode(person.getPassword()),
                role.getBody());
        return this.personRepository.save(buff);
    }

    /**
     * PATCH method for updating object of Person
     * @param person - object of Person
     */
    @PatchMapping("/")
    @Validated(Operation.OnUpdate.class)
    public void update(@Valid @RequestBody Person person) throws InvocationTargetException, IllegalAccessException {
        var current = personRepository.findById(person.getId());
        if (current.isEmpty()) {
            throw new NullPointerException("Person not found");
        }
        var buffPerson = current.get();
        var methods = buffPerson.getClass().getDeclaredMethods();
        var namePerMethod = new HashMap<String, Method>();
        for (var method : methods) {
            var name = method.getName();
            if (name.startsWith("get") || name.startsWith("set")) {
                namePerMethod.put(name, method);
            }
        }
        for (var name : namePerMethod.keySet()) {
            if (name.startsWith("get")) {
                var getMethod = namePerMethod.get(name);
                var setMethod = namePerMethod.get(name.replace("get", "set"));
                if (setMethod == null) {
                    throw new NullPointerException("Invalid properties");
                }
                var newValue = getMethod.invoke(person);
                if (name.equals("getPassword")) {
                    newValue = encoder.encode((String) newValue);
                }
                if (newValue != null) {
                    setMethod.invoke(buffPerson, newValue);
                }
            }
        }
        personRepository.save(buffPerson);
    }

    /**
     * DELETE method for deleting object of Person by person ID
     * @param id - person ID
     * @return
     */
    @DeleteMapping("/{id}")
    @Validated(Operation.OnDelete.class)
    public void delete(@Valid @PathVariable int id) {
        if (this.personRepository.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found");
        }
        Person person = new Person();
        person.setId(id);
        this.personRepository.delete(person);
    }
}
