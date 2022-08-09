package ru.job4j.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.job4j.chat.model.Person;
import ru.job4j.chat.model.Role;
import ru.job4j.chat.repository.PersonRepository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Logic for working with object of Room
 */
@Service
@Slf4j
public class UserService {

    private RestTemplate rest;

    /**
     * DAO for objects of Person
     */
    private final PersonRepository personRepository;

    private BCryptPasswordEncoder encoder;
    /**
     * URI for getting object of Role by role ID
     */
    private static final String ROLE_API_ID = "http://localhost:8080/role/{id}";

    public UserService(RestTemplate rest, PersonRepository personRepository,
                          BCryptPasswordEncoder encoder) {
        this.rest = rest;
        this.personRepository = personRepository;
        this.encoder = encoder;
    }

    /**
     * Method for getting all Persons
     * @return List of persons
     */
    public List<Person> findAllPerson() {
        String anchor = UUID.randomUUID().toString();
        List<Person> personList = StreamSupport.stream(
                this.personRepository.findAll().spliterator(), false
        ).collect(Collectors.toList());
        if (personList == null) {
            throw new NullPointerException("An internal error has occurred. Please try again later or contact technical support with the 'anchor'. anchor: " + anchor);
        }
        return personList;
    }

    /**
     * Method for getting Person by person ID
     * @param personId - person ID
     * @return object of Person
     */
    public Person findPersonById(int personId) {
        String anchor = UUID.randomUUID().toString();
        Optional<Person> person = personRepository.findById(personId);
        if (person.isEmpty()) {
            throw new IllegalArgumentException("Person not found. Actual parameters: person ID - " + personId + ". Please contact technical support with the 'anchor'. anchor: " + anchor);
        }
        return person.get();
    }

    /**
     * Method for getting Person by username of person
     * @param username - username of Person
     * @return object of Person
     */
    public Person findPersonByUsername(String username) {
        String anchor = UUID.randomUUID().toString();
        Optional<Person> person = personRepository.findByUsername(username);
        if (person.isEmpty()) {
            throw new IllegalArgumentException("Person not found. Actual parameters: username of person - " + username + ". Please contact technical support with the 'anchor'. anchor: " + anchor);
        }
        return person.get();
    }

    /**
     * Method for signing up new person
     * @param roleId - role ID
     * @param person - object of Person
     * @param jwtToken - JWT token (String)
     * @return object of Person
     */
    public Person singUpPerson(int roleId, Person person, String jwtToken) {
        if (person.getUsername() == null || person.getPassword() == null) {
            throw new NullPointerException("Username or Password field's is empty");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", jwtToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<Role> role = rest.exchange(ROLE_API_ID, HttpMethod.GET, entity, Role.class, roleId);
        Person signedUpPerson = Person.of(
                person.getUsername(),
                encoder.encode(person.getPassword()),
                role.getBody());
        return personRepository.save(signedUpPerson);
    }

    /**
     * Method for updating object of Person
     * @param person object of Person
     */
    public void updatePerson(Person person) {
        String anchor = UUID.randomUUID().toString();
        var currentPerson = personRepository.findById(person.getId());
        if (currentPerson.isEmpty()) {
            throw new IllegalArgumentException("Person not found. Actual parameters: person ID - " + person.getId() + ". Please contact technical support with the 'anchor'. anchor: " + anchor);
        }
        var tempPerson = currentPerson.get();
        var methods = tempPerson.getClass().getDeclaredMethods();
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
                    throw new NullPointerException("An internal error has occurred. Please try again later or contact technical support with the 'anchor'. anchor: " + anchor);
                }
                Object newValue = null;
                try {
                    newValue = getMethod.invoke(person);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                if (name.equals("getPassword")) {
                    newValue = encoder.encode((String) newValue);
                }
                if (newValue != null) {
                    try {
                        setMethod.invoke(tempPerson, newValue);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new NullPointerException("An internal error has occurred. Please try again later or contact technical support with the 'anchor'. anchor: " + anchor);
                    }
                }
            }
        }
        personRepository.save(tempPerson);
    }

    /**
     * Method for deleting object of Person by person ID
     * @param personId person ID
     */
    public void deletePersonById(int personId) {
        String anchor = UUID.randomUUID().toString();
        Optional<Person> foundPerson = personRepository.findById(personId);
        if (foundPerson.isEmpty()) {
            throw new IllegalArgumentException("Person not found. Actual parameters: person ID - " + personId + ". Please contact technical support with the 'anchor'. anchor: " + anchor);
        }
        Person person = new Person();
        person.setId(personId);
        personRepository.delete(person);
    }
}
