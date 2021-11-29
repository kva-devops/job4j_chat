package ru.job4j.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.job4j.chat.model.Message;
import ru.job4j.chat.model.Person;
import ru.job4j.chat.model.Room;
import ru.job4j.chat.repository.MessageRepository;
import ru.job4j.chat.repository.PersonRepository;
import ru.job4j.chat.repository.RoomRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private RestTemplate rest;

    private final PersonRepository personRepository;

    private final RoomRepository roomRepository;

    private final MessageRepository messageRepository;

    public MessageController(PersonRepository personRepository,
                             RoomRepository roomRepository,
                             final MessageRepository messageRepository) {
        this.personRepository = personRepository;
        this.roomRepository = roomRepository;
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

    @PostMapping("/")
    public ResponseEntity<Message> create(@RequestBody Message message) {
        Optional<Room> room = roomRepository.findById(1);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Person person = personRepository.findByUsername(username);
        Message buff = Message.of(message.getText(), room.get(), person);
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
