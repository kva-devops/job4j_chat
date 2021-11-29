package ru.job4j.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.job4j.chat.model.Message;
import ru.job4j.chat.model.Person;
import ru.job4j.chat.model.Room;
import ru.job4j.chat.repository.MessageRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
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
        return StreamSupport.stream(
                this.messageRepository.findAllByUserId(id).spliterator(), false
        ).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> findById(@PathVariable int id) {
        var message = this.messageRepository.findById(id);
        return new ResponseEntity<Message>(
                message.orElse(new Message()),
                message.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    @PostMapping("/room/{id}")
    public ResponseEntity<Message> create(@PathVariable("id") int id, @RequestBody Message message, @RequestHeader("Authorization") String token) {
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

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody Message message) {
        Message buff = this.messageRepository.findById(message.getId()).get();
        buff.setText(message.getText());
        this.messageRepository.save(buff);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Message message = new Message();
        message.setId(id);
        this.messageRepository.delete(message);
        return ResponseEntity.ok().build();
    }
}
