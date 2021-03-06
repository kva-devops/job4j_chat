package ru.job4j.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.chat.handlers.Operation;
import ru.job4j.chat.model.Room;
import ru.job4j.chat.repository.RoomRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@Validated
@RequestMapping("/room")
public class RoomController {

    private final ObjectMapper mapper;

    private final RoomRepository roomRepository;

    public RoomController(ObjectMapper mapper, RoomRepository roomRepository) {
        this.mapper = mapper;
        this.roomRepository = roomRepository;
    }

    @GetMapping("/")
    public List<Room> findAll() {
        return StreamSupport.stream(
                this.roomRepository.findAll().spliterator(), false
        ).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> findById(@PathVariable int id) {
        var room = this.roomRepository.findById(id);
        if (room.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found");
        }
        return new ResponseEntity<>(
                room.get(),
                HttpStatus.OK
        );
    }

    @PostMapping("/")
    @Validated(Operation.OnCreate.class)
    public ResponseEntity<Room> create(@Valid @RequestBody Room room) {
        if (room.getName() == null) {
            throw new NullPointerException("Name field is empty");
        }
        if (room.getName().contains("stop-word")) {
            throw new IllegalArgumentException("You cant use stop-word in room name");
        }
        Room buff = Room.of(room.getName());
        return new ResponseEntity<>(
                this.roomRepository.save(buff),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/")
    @Validated(Operation.OnUpdate.class)
    public ResponseEntity<Void> update(@Valid @RequestBody Room room) throws InvocationTargetException, IllegalAccessException {
        var current = roomRepository.findById(room.getId());
        if (current.isEmpty()) {
            throw new NullPointerException("Room not found");
        }
        var buffRoom = current.get();
        var methods = buffRoom.getClass().getDeclaredMethods();
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
                var newValue = getMethod.invoke(room);
                if (newValue != null) {
                    setMethod.invoke(buffRoom, newValue);
                }
            }
        }
        roomRepository.save(buffRoom);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Validated(Operation.OnDelete.class)
    public ResponseEntity<Void> delete(@Valid @PathVariable int id) {
        if (this.roomRepository.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found");
        }
        Room room = new Room();
        room.setId(id);
        this.roomRepository.delete(room);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public void exceptionHandler(
            Exception e,
            HttpServletRequest req,
            HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpStatus.BAD_REQUEST.value());
        resp.setContentType("application/json");
        resp.getWriter().write(mapper.writeValueAsString(new HashMap<>() { {
            put("message", e.getMessage());
            put("type", e.getClass());
        } }));
    }
}
