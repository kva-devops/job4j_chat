package ru.job4j.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.chat.handlers.Operation;
import ru.job4j.chat.model.Room;
import ru.job4j.chat.repository.RoomRepository;

import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Rest controller for working with models of Room
 */
@RestController
@Validated
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {

    /**
     * DAO for models of Room
     */
    private final RoomRepository roomRepository;

    /**
     * GET method for getting all available Rooms
     * @return List of rooms
     */
    @GetMapping("/")
    public List<Room> findAll() {
        return StreamSupport.stream(
                this.roomRepository.findAll().spliterator(), false
        ).collect(Collectors.toList());
    }

    /**
     * GET method for getting Room object by room ID
     * @param id - room ID
     * @return Room object
     */
    @GetMapping("/{id}")
    public Room findById(@PathVariable int id) {
        var room = this.roomRepository.findById(id);
        if (room.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found");
        }
        return room.get();
    }

    /**
     * GET method for creating new Room
     * @param room - object of Room
     * @return object of Room
     */
    @PostMapping("/")
    @Validated(Operation.OnCreate.class)
    public Room create(@Valid @RequestBody Room room) {
        if (room.getName() == null) {
            throw new NullPointerException("Name field is empty");
        }
        if (room.getName().contains("stop-word")) {
            throw new IllegalArgumentException("You cant use stop-word in room name");
        }
        Room buff = Room.of(room.getName());
        return this.roomRepository.save(buff);
    }

    /**
     * PATCH method for updating object of Room
     * @param room object of Room
     */
    @PatchMapping("/")
    @Validated(Operation.OnUpdate.class)
    public void update(@Valid @RequestBody Room room) throws InvocationTargetException, IllegalAccessException {
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
    }

    /**
     * DELETE method for deleting object of Room by room ID
     * @param id - room ID
     */
    @DeleteMapping("/{id}")
    @Validated(Operation.OnDelete.class)
    public void delete(@Valid @PathVariable int id) {
        if (this.roomRepository.findById(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found");
        }
        Room room = new Room();
        room.setId(id);
        this.roomRepository.delete(room);
    }
}
