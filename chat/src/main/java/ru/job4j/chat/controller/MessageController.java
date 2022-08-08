package ru.job4j.chat.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.job4j.chat.handlers.Operation;
import ru.job4j.chat.model.Message;
import ru.job4j.chat.service.MessageService;

import javax.validation.Valid;
import java.util.List;

/**
 * Rest controller for working with models of Message
 */
@RestController
@Validated
@RequestMapping("/message")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * GET method for getting all messages
     * @return List of messages
     */
    @GetMapping("/")
    public List<Message> findAll() {
        return messageService.findAllMessages();
    }

    /**
     * GET method for getting List of messages by user ID
     * @param id user ID
     * @return List of messages
     */
    @GetMapping("/byUserId/{id}")
    public List<Message> findByUserId(@PathVariable int id) {
        return messageService.findMessagesByUserId(id);
    }

    /**
     * GET method for getting message by message ID
     * @param id message ID
     * @return Message
     */
    @GetMapping("/{id}")
    public Message findById(@PathVariable int id) {
            return messageService.findMessageByMessageId(id);
    }

    /**
     * POST method for creating new message
     * @param id - room ID
     * @param message - Message
     * @param token - String token JWT
     * @return Created object of Message
     */
    @PostMapping("/room/{id}")
    @Validated(Operation.OnCreate.class)
    public Message create(@PathVariable("id") int id, @Valid @RequestBody Message message, @RequestHeader("Authorization") String token) {
        return messageService.addMessage(id, message, token);
    }

    /**
     * PATCH method for updating message
     * @param message - object of Message
     */
    @PatchMapping("/")
    @Validated(Operation.OnUpdate.class)
    public void update(@Valid @RequestBody Message message) {
        messageService.updateMessage(message);
    }

    /**
     * DELETE method for deleting message
     * @param id - message ID
     */
    @DeleteMapping("/{id}")
    @Validated(Operation.OnDelete.class)
    public void delete(@Valid @PathVariable int id) {
        messageService.deleteMessage(id);
    }
}
