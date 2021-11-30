package ru.job4j.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.chat.model.Person;
import ru.job4j.chat.model.Role;
import ru.job4j.chat.repository.PersonRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private RestTemplate rest;

    private BCryptPasswordEncoder encoder;

    private final PersonRepository personRepository;

    private static final String ROLE_API_ID = "http://localhost:8080/role/{id}";

    public UserController(PersonRepository personRepository,
                          BCryptPasswordEncoder encoder) {
        this.personRepository = personRepository;
        this.encoder = encoder;
    }

    @GetMapping("/all")
    public List<Person> findAll() {
        return StreamSupport.stream(
                this.personRepository.findAll().spliterator(), false
        ).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        var person = this.personRepository.findById(id);
        if (person.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found");
        }
        return new ResponseEntity<>(
                person.get(),
                HttpStatus.OK
        );
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<Person> findByUsername(@PathVariable String username) {
        var person = this.personRepository.findByUsername(username);
        return new ResponseEntity<>(
                person.orElse(new Person()),
                person.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    @PostMapping("/sign-up/role/{id}")
    public ResponseEntity<Person> signUp(
            @PathVariable("id") int id,
            @RequestBody Person person,
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
        return new ResponseEntity<>(
                this.personRepository.save(buff),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Person person) {
        if (person.getUsername() == null || person.getPassword() == null) {
            throw new NullPointerException("Username or Password field's is empty");
        }
        Person buff = this.personRepository.findById(person.getId()).get();
        buff.setUsername(person.getUsername());
        buff.setPassword(person.getPassword());
        this.personRepository.save(buff);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        if (this.personRepository.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found");
        }
        Person person = new Person();
        person.setId(id);
        this.personRepository.delete(person);
        return ResponseEntity.ok().build();
    }
}
