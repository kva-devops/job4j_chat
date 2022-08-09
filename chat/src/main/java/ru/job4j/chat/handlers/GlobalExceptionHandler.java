package ru.job4j.chat.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.job4j.chat.model.ErrorMessages;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Controller for handling all exceptions that occur in the application
 */
@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ObjectMapper mapper;

    /**
     * Handler for general Exception.class
     * @param e - exception object
     * @param response - HttpServletResponse object, used to form response
     * @throws IOException
     */
    @ExceptionHandler(value = {Exception.class})
    public void handleException(Exception e, HttpServletResponse response) throws IOException {
        setResponse(e, response, "Unexpected error");
    }

    /**
     * Handler for NPE.class
     * @param e - exception object
     * @param response - HttpServletResponse object, used to form response
     * @throws IOException
     */
    @ExceptionHandler(value = {NullPointerException.class})
    public void handleInternalException(Exception e, HttpServletResponse response) throws IOException {
        setResponse(e, response, "Internal error");
    }

    /**
     * Handler for IllegalArgumentException.class
     * @param e - exception object
     * @param response - HttpServletResponse object, used to form response
     * @throws IOException
     */
    @ExceptionHandler(value = {IllegalArgumentException.class})
    public void handleValidationException(Exception e, HttpServletResponse response) throws IOException {
        setResponse(e, response, "Validation error");
    }

    /**
     * Private method for generating a response
     * @param e - instance of exception
     * @param response - HttpServletResponse object
     * @param shortMessage - object containing a description of the exception type
     * @throws IOException
     */
    private void setResponse(Exception e, HttpServletResponse response, String shortMessage) throws IOException {
        Map<String, String> messagesMap = extractAnchor(e, shortMessage);
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(mapper.writeValueAsString(
                new ErrorMessages(shortMessage, messagesMap.get("LongMessage"), messagesMap.get("Anchor")).getError()
        ));
    }

    /**
     * Private method for extracting the anchor from the exception message
     * @param e- instance of exception
     * @param shortMessage - object containing a description of the exception type
     * @return Map object containing a long message and an anchor
     */
    private Map<String, String> extractAnchor(Exception e, String shortMessage) {
        Map<String, String> messageMap = new HashMap<>();
        String exceptionMessage = e.getMessage();
        if ("Unexpected error".equals(shortMessage) && (exceptionMessage == null || !exceptionMessage.contains("anchor: "))) {
            String anchor = UUID.randomUUID().toString();
            log.error("Unexpected error. Anchor: " + anchor, e);
            messageMap.put("LongMessage", "An unexpected error has occured. Please contact technical support with 'Anchor'");
            messageMap.put("Anchor", anchor);
        } else {
            String[] messages = exceptionMessage.split("anchor:");
            log.warn("Anchor: {}, StackTrace: {}", messages[1], e.getStackTrace());
            messageMap.put("LongMessage", messages[0]);
            messageMap.put("Anchor", messages[1]);
        }
        return messageMap;
    }
}
