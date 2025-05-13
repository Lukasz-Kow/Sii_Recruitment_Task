package sii.task.recruitment.handler;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import sii.task.recruitment.exception.*;

import java.util.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            CollectionBoxNotFoundException.class,
            FundraisingEventNotFoundException.class,
            ExchangeRateNotFoundException.class
    })
    public ResponseEntity<Map<String, Object>> handleNotFoundException(Exception ex) {
        return buildErrorResponse("Not Found", HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({
            CollectionBoxExistsException.class,
            CollectionBoxIsNotEmptyException.class,
            CurrencyConversionException.class
    })
    public ResponseEntity<Map<String, Object>> handleBadRequestException(Exception ex) {
        return buildErrorResponse("Bad Request", HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler({IllegalAmountException.class})
    public ResponseEntity<Map<String, Object>> handleIllegalAmountException(IllegalAmountException ex) {
        return buildErrorResponse("Illegal Amount", HttpStatus.BAD_REQUEST, "Amount must be greater than 0.");
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        Map<String, Object> response = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(field, errorMessage);
        });

        response.put("error", "Bad Request");
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("errors", errors);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Map<String, Object> response = new HashMap<>();
        String message = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();

        if (message != null && message.contains("unique") || Objects.requireNonNull(message).contains("UNIQUE")) {
            response.put("error", "Bad Request");
            response.put("status", HttpStatus.BAD_REQUEST.value());
            response.put("message", "Identifier already exists. Please choose a different identifier.");
            return ResponseEntity.badRequest().body(response);
        }

        response.put("error", "Internal Server Error");
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("message", "Internal error occurred. Please try again later.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler({
            CollectionBoxEmptyException.class,
            CollectionBoxNotAssignedException.class
    })
    public ResponseEntity<Map<String, Object>> handleTransferExceptions(RuntimeException ex) {
        Map<String, Object> response = new HashMap<>();

        response.put("error", "Transfer Error");
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(String error, HttpStatus status, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", error);
        response.put("status", status.value());
        response.put("message", message);
        return new ResponseEntity<>(response, status);
    }
}
