package ru.job4j.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.job4j.chat.model.Room;
import ru.job4j.chat.repository.RoomRepository;

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
@RequiredArgsConstructor
public class RoomService {
    /**
     * DAO for models of Room
     */
    private final RoomRepository roomRepository;

    /**
     * Method for getting all available Rooms
     * @return List of rooms
     */
    public List<Room> findAllRooms() {
        String anchor = UUID.randomUUID().toString();
        List<Room> roomList = StreamSupport.stream(
                this.roomRepository.findAll().spliterator(), false
        ).collect(Collectors.toList());
        if (roomList == null) {
            throw new NullPointerException("An internal error has occurred. Please try again later or contact technical support with the 'anchor'. anchor: " + anchor);
        }
        return roomList;
    }

    /**
     * Method for getting Room by room ID
     * @param roomId - room ID
     * @return object of Room
     */
    public Room findRoomById(int roomId) {
        String anchor = UUID.randomUUID().toString();
        Optional<Room> room = roomRepository.findById(roomId);
        if (room.isEmpty()) {
            throw new IllegalArgumentException("Room not found. Actual parameters: room ID - " + roomId + ". Please contact technical support with the 'anchor'. anchor: " + anchor);
        }
        return room.get();
    }

    /**
     * Method for creating new Room
     * @param room - object of Room
     * @return object of Room
     */
    public Room addRoom(Room room) {
        String anchor = UUID.randomUUID().toString();
        if (room.getName() == null) {
            throw new IllegalArgumentException("Name of room is empty. Please contact technical support with the 'anchor'. anchor: " + anchor);
        }
        Room addedRoom = roomRepository.save(Room.of(room.getName()));
        if (addedRoom == null) {
            throw new NullPointerException("An internal error has occurred. Please try again later or contact technical support with the 'anchor'. anchor: " + anchor);
        }
        return addedRoom;
    }

    /**
     * Method for updating object of Room
     * @param room - object of Room
     */
    public void updateRoom(Room room) {
        String anchor = UUID.randomUUID().toString();
        var currentRoom = roomRepository.findById(room.getId());
        if (currentRoom.isEmpty()) {
            throw new IllegalArgumentException("Room not found. Actual parameters: room ID - " + room.getId() + ". Please contact technical support with the 'anchor'. anchor: " + anchor);
        }
        var tempRoom = currentRoom.get();
        var methods = tempRoom.getClass().getDeclaredMethods();
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
                Object newValue = null;
                try {
                    newValue = getMethod.invoke(room);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new NullPointerException("An internal error has occurred. Please try again later or contact technical support with the 'anchor'. anchor: " + anchor);
                }
                if (newValue != null) {
                    try {
                        setMethod.invoke(tempRoom, newValue);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new NullPointerException("An internal error has occurred. Please try again later or contact technical support with the 'anchor'. anchor: " + anchor);
                    }
                }
            }
        }
        roomRepository.save(tempRoom);
    }

    /**
     * Method for deleting object of Room
     * @param roomId - room ID
     */
    public void deleteRoomById(int roomId) {
        String anchor = UUID.randomUUID().toString();
        Optional<Room> foundRoom = roomRepository.findById(roomId);
        if (foundRoom.isEmpty()) {
            throw new IllegalArgumentException("Room not found. Actual parameters: room ID - " + roomId + ". Please contact technical support with the 'anchor'. anchor: " + anchor);
        }
        Room room = new Room();
        room.setId(roomId);
        roomRepository.delete(room);
    }
}
