package ru.job4j.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.job4j.chat.model.Message;
import ru.job4j.chat.model.Person;
import ru.job4j.chat.model.Room;
import ru.job4j.chat.repository.MessageRepository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Logic for working with object of Message
 */
@Service
@Slf4j
public class MessageService {

    private RestTemplate rest;

    private static final String PERSON_API_USERNAME = "http://localhost:8080/users/username/{username}";

    private static final String ROOM_API_ID = "http://localhost:8080/room/{id}";

    /**
     * DAO for messages
     */
    private final MessageRepository messageRepository;

    public MessageService(RestTemplate rest, MessageRepository messageRepository) {
        this.rest = rest;
        this.messageRepository = messageRepository;
    }

    /**
     * Method for getting all messages
     * @return List of messages
     */
    public List<Message> findAllMessages() {
        String anchor = UUID.randomUUID().toString();
        List<Message> messageList = StreamSupport.stream(
                this.messageRepository.findAll().spliterator(), false
        ).collect(Collectors.toList());
        if (messageList == null) {
            throw new NullPointerException("An internal error has occurred. Please try again later or contact technical support with the 'anchor'. anchor: " + anchor);
        }
        return messageList;
    }

    /**
     * Method for getting List of messages by user ID
     * @param userId - user ID
     * @return List of messages
     */
    public List<Message> findMessagesByUserId(int userId) {
        String anchor = UUID.randomUUID().toString();
        List<Message> messageList = this.messageRepository.findAllMessagesByUserId(userId);
        if (messageList == null) {
            throw new NullPointerException("An internal error has occurred. Please try again later or contact technical support with the 'anchor'. anchor: " + anchor);
        }
        return messageList;
    }

    /**
     * Method for getting message by message ID
     * @param messageId - message ID
     * @return - object of Message
     */
    public Message findMessageByMessageId(int messageId) {
        String anchor = UUID.randomUUID().toString();
        Optional<Message> message = this.messageRepository.findById(messageId);
        if (message.isEmpty()) {
            throw new IllegalArgumentException("Message not found. Actual parameters: message ID - " + messageId + ". Please contact technical support with the 'anchor'. anchor: " + anchor);
        }
        return message.get();
    }

    /**
     * Method for creating new message
     * @param roomId - room ID
     * @param message - object of Message
     * @param token - JWT token (String)
     * @return Message
     */
    public Message addMessage(int roomId, Message message, String token) {
        String anchor = UUID.randomUUID().toString();
        if (message.getText() == null) {
            throw new IllegalArgumentException("Text of message is empty. Please contact technical support with the 'anchor'. anchor: " + anchor);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<Room> room = rest.exchange(ROOM_API_ID, HttpMethod.GET, entity, Room.class, roomId);
        if (room.getBody() == null) {
            throw new NullPointerException("An internal error has occurred. Please try again later or contact technical support with the 'anchor'. anchor: " + anchor);
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ResponseEntity<Person> person = rest.exchange(PERSON_API_USERNAME, HttpMethod.GET, entity, Person.class, username);
        if (person.getBody() == null) {
            throw new NullPointerException("An internal error has occurred. Please try again later or contact technical support with the 'anchor'. anchor: " + anchor);
        }
        Message addedMessage = Message.of(message.getText(), room.getBody(), person.getBody());
        Message response = this.messageRepository.save(addedMessage);
        if (response == null) {
            throw new NullPointerException("An internal error has occurred. Please try again later or contact technical support with the 'anchor'. anchor: " + anchor);
        }
        return response;
    }

    /**
     * Method for updating message
     * @param message - object of Message
     */
    public void updateMessage(Message message) {
        String anchor = UUID.randomUUID().toString();
        var current = messageRepository.findById(message.getId());
        if (current.isEmpty()) {
            throw new IllegalArgumentException("Message not found. Actual parameters: message ID - " + message.getId() + ". Please contact technical support with the 'anchor'. anchor: " + anchor);
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
                    throw new NullPointerException("An internal error has occurred. Please try again later or contact technical support with the 'anchor'. anchor: " + anchor);
                }
                Object newValue = null;
                try {
                    newValue = getMethod.invoke(message);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                if (name.equals("getCreated")) {
                    newValue = new Timestamp(System.currentTimeMillis());
                }
                if (newValue != null) {
                    try {
                        setMethod.invoke(buffMessage, newValue);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        messageRepository.save(buffMessage);
    }

    /**
     * Method for deleting message
     * @param messageId - message ID
     */
    public void deleteMessage(int messageId) {
        String anchor = UUID.randomUUID().toString();
        Optional<Message> foundMessage = messageRepository.findById(messageId);
        if (foundMessage.isEmpty()) {
            throw new IllegalArgumentException("Message not found. Actual parameters: message ID - " + messageId + ". Please contact technical support with the 'anchor'. anchor: " + anchor);
        }
        Message message = new Message();
        message.setId(messageId);
        messageRepository.delete(message);
    }
}
