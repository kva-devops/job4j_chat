package ru.job4j.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.job4j.chat.handlers.Operation;
import ru.job4j.chat.model.Room;
import ru.job4j.chat.service.RoomService;

import javax.validation.Valid;
import java.util.List;

/**
 * Rest controller for working with models of Room
 */
@RestController
@Validated
@RequestMapping("/room")
@RequiredArgsConstructor
public class RoomController {

    /**
     * Business login for working with object of Room
     */
    private final RoomService roomService;

    /**
     * GET method for getting all available Rooms
     * @return List of rooms
     */
    @GetMapping("/")
    public List<Room> findAll() {
        return roomService.findAllRooms();
    }

    /**
     * GET method for getting Room object by room ID
     * @param id - room ID
     * @return Room object
     */
    @GetMapping("/{id}")
    public Room findById(@PathVariable int id) {
        return roomService.findRoomById(id);
    }

    /**
     * GET method for creating new Room
     * @param room - object of Room
     * @return object of Room
     */
    @PostMapping("/")
    @Validated(Operation.OnCreate.class)
    public Room create(@Valid @RequestBody Room room) {
        return roomService.addRoom(room);
    }

    /**
     * PATCH method for updating object of Room
     * @param room object of Room
     */
    @PatchMapping("/")
    @Validated(Operation.OnUpdate.class)
    public void update(@Valid @RequestBody Room room) {
        roomService.updateRoom(room);
    }

    /**
     * DELETE method for deleting object of Room by room ID
     * @param id - room ID
     */
    @DeleteMapping("/{id}")
    @Validated(Operation.OnDelete.class)
    public void delete(@Valid @PathVariable int id) {
        roomService.deleteRoomById(id);
    }
}
