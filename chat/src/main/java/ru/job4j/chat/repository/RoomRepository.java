package ru.job4j.chat.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.chat.model.Room;

/**
 * DAO interface for Rooms
 */
public interface RoomRepository extends CrudRepository<Room, Integer> {
}
