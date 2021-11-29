package ru.job4j.chat.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.job4j.chat.model.Message;

import java.util.Arrays;
import java.util.List;

public interface MessageRepository extends CrudRepository<Message, Integer> {

    @Query("SELECT m FROM Message m "
            + "JOIN FETCH m.person p "
            + "WHERE p.id = :id")
    List<Message> findAllByUserId(@Param("id") int id);
}
