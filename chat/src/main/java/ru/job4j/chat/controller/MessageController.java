package ru.job4j.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.chat.handlers.Operation;
import ru.job4j.chat.model.Message;
import ru.job4j.chat.model.Person;
import ru.job4j.chat.model.Room;
import ru.job4j.chat.repository.MessageRepository;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@Validated
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private RestTemplate rest;

    private static final String PERSON_API_USERNAME = "http://localhost:8080/users/username/{username}";

    private static final String ROOM_API_ID = "http://localhost:8080/room/{id}";

    private final MessageRepository messageRepository;

    public MessageController(final MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @GetMapping("/")
    public List<Message> findAll() {
        return StreamSupport.stream(
                this.messageRepository.findAll().spliterator(), false
        ).collect(Collectors.toList());
    }

    @GetMapping("/byUserId/{id}")
    public List<Message> findByUserId(@PathVariable int id) {
        List<Message> buff = this.messageRepository.findAllByUserId(id);
        if (buff.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found");
        }
        return StreamSupport.stream(
                buff.spliterator(), false
        ).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> findById(@PathVariable int id) {
        var message = this.messageRepository.findById(id);
        if (message.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found");
        }
        return new ResponseEntity<Message>(
                message.get(),
                HttpStatus.OK
        );

    }

    @PostMapping("/room/{id}")
    @Validated(Operation.OnCreate.class)
    public ResponseEntity<Message> create(@PathVariable("id") int id, @Valid @RequestBody Message message, @RequestHeader("Authorization") String token) {
        if (message.getText() == null) {
            throw new NullPointerException("Text field is empty");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<Room> room = rest.exchange(ROOM_API_ID, HttpMethod.GET, entity, Room.class, id);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity<Person> person = rest.exchange(PERSON_API_USERNAME, HttpMethod.GET, entity, Person.class, username);
        Message buff = Message.of(message.getText(), room.getBody(), person.getBody());
        return new ResponseEntity<Message>(
                this.messageRepository.save(buff),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/")
    @Validated(Operation.OnUpdate.class)
    public ResponseEntity<Void> update(@Valid @RequestBody Message message) throws InvocationTargetException, IllegalAccessException {
        var current = messageRepository.findById(message.getId());
        if (current.isEmpty()) {
            throw new NullPointerException("Message not found");
        }
        var buffMessage = current.get();
        var methods = buffMessage.getClass().getDeclaredMethods();
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
                var newValue = getMethod.invoke(message);
                if (name.equals("getCreated")) {
                    newValue = new Timestamp(System.currentTimeMillis());
                }
                if (newValue != null) {
                    setMethod.invoke(buffMessage, newValue);
                }
            }
        }
        messageRepository.save(buffMessage);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Validated(Operation.OnDelete.class)
    public ResponseEntity<Void> delete(@Valid @PathVariable int id) {
        if (this.messageRepository.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found");
        }
        Message message = new Message();
        message.setId(id);
        this.messageRepository.delete(message);
        return ResponseEntity.ok().build();
    }
}
