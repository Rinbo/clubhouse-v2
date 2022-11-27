package nu.borjessons.clubhouse.impl.controller.exception;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;
import nu.borjessons.clubhouse.impl.dto.ErrorMessage;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler {
  private static String getMessageOrDefault(Exception exception, String defaultMessage) {
    String message = exception.getMessage();
    if (message != null) return message;
    return defaultMessage;
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorMessage> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest req) {
    log.debug("", ex);

    ErrorMessage errorMessage = new ErrorMessage(ex.getMessage(), req.getRequestURI(), 403);
    return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler({
      javax.validation.ConstraintViolationException.class,
      org.hibernate.exception.ConstraintViolationException.class
  })
  public ResponseEntity<ErrorMessage> handleConstraintViolationException(Exception ex, HttpServletRequest req) {
    log.debug("", ex);

    ErrorMessage errorMessage = new ErrorMessage(getMessageOrDefault(ex, "Database constraint violation"),
        req.getRequestURI(), HttpStatus.CONFLICT.value());
    return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.CONFLICT);
  }

  @ExceptionHandler(IOException.class)
  public ResponseEntity<ErrorMessage> handleIOException(IOException ex, HttpServletRequest req) {
    log.debug("", ex);

    ErrorMessage errorMessage = new ErrorMessage(ex.getMessage(), req.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorMessage> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest req) {
    log.debug("", ex);

    ErrorMessage errorMessage = new ErrorMessage(ex.getMessage(), req.getRequestURI(), HttpStatus.BAD_REQUEST.value());
    return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorMessage> handleIllegalStateException(IllegalStateException ex, HttpServletRequest req) {
    log.debug("", ex);

    ErrorMessage errorMessage = new ErrorMessage(ex.getMessage(), req.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(InternalAuthenticationServiceException.class)
  public ResponseEntity<ErrorMessage> handleInternalAuthenticationServiceException(Exception ex, HttpServletRequest req) {
    log.debug("", ex);

    ErrorMessage errorMessage = new ErrorMessage(getMessageOrDefault(ex, "Forbidden access"), req.getRequestURI(), 403);
    return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorMessage> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest req) {
    log.debug("", ex);

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> errors.put(((FieldError) error).getField(), error.getDefaultMessage()));

    ErrorMessage errorMessage = new ErrorMessage("Validation failure. Check your inputs", errors, req.getRequestURI(),
        HttpStatus.BAD_REQUEST.value());
    return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({NoResultException.class, NoSuchElementException.class})
  public ResponseEntity<ErrorMessage> handleNoSuchElementException(RuntimeException ex, HttpServletRequest req) {
    log.debug("", ex);

    ErrorMessage errorMessage = new ErrorMessage(ex.getMessage(), req.getRequestURI(), HttpStatus.NOT_FOUND.value());
    return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorMessage> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest req) {
    log.debug("", ex);

    ErrorMessage errorMessage = new ErrorMessage(ex.getReason(), req.getRequestURI(), ex.getStatus().value());
    return new ResponseEntity<>(errorMessage, new HttpHeaders(), ex.getStatus());
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorMessage> handleRuntimeException(RuntimeException ex, HttpServletRequest req) {
    log.debug("", ex);

    ErrorMessage errorMessage = new ErrorMessage(ex.getMessage(), req.getRequestURI(), 500);
    return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<ErrorMessage> handleUsernameNotFoundException(UsernameNotFoundException ex, HttpServletRequest req) {
    log.debug("", ex);

    ErrorMessage errorMessage = new ErrorMessage(ex.getMessage(), req.getRequestURI(), HttpStatus.NOT_FOUND.value());
    return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.NOT_FOUND);
  }
}
