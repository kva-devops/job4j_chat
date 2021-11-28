package ru.job4j.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.job4j.chat.model.Message;
import ru.job4j.chat.model.Person;
import ru.job4j.chat.model.Room;
import ru.job4j.chat.repository.MessageRepository;
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

    private static final String PERSON_API_ID = "http://localhost:8080/person/{id}";

    private final MessageRepository messageRepository;

    private final RoomRepository roomRepository;


    public MessageController(final MessageRepository messageRepository,
                             final RoomRepository roomRepository
            ) {
        this.messageRepository = messageRepository;
        this.roomRepository = roomRepository;
    }

    @GetMapping("/")
    public List<Message> findAll() {
        return StreamSupport.stream(
                this.messageRepository.findAll().spliterator(), false
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
        Optional<Room> room = this.roomRepository.findById(1);
        Person person = rest.getForObject(PERSON_API_ID, Person.class, 2);
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
