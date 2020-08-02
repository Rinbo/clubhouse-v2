package nu.borjessons.clubhouse.controller.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler {
	
	@ExceptionHandler(value = {javax.validation.ConstraintViolationException.class, org.hibernate.exception.ConstraintViolationException.class})
	public ResponseEntity<Object> handleSpecificExceptions(Exception ex, HttpServletRequest req) {

		log.debug(stringifyStacktrace(ex));
		
		String errorDescription = "A resource already exists in the database with provided parameters";
		
		ErrorMessage errorMessage = new ErrorMessage(errorDescription, req.getRequestURI(), HttpStatus.CONFLICT.value());
		
		return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.CONFLICT);
	}
    
    @ExceptionHandler({ ResponseStatusException.class })
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest req) {
    	
		log.debug(stringifyStacktrace(ex));
		
		ErrorMessage errorMessage = new ErrorMessage(ex.getReason(), req.getRequestURI(), ex.getStatus().value());
		
		return new ResponseEntity<>(errorMessage, new HttpHeaders(), ex.getStatus());
    }
    
    @ExceptionHandler({ IllegalArgumentException.class })
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest req) {
    	
		log.debug(stringifyStacktrace(ex));
		
		ErrorMessage errorMessage = new ErrorMessage(ex.getMessage(), req.getRequestURI(), HttpStatus.BAD_REQUEST.value());
		
		return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler({ IllegalStateException.class })
    public ResponseEntity<Object> handleIllegalStateException(IllegalArgumentException ex, HttpServletRequest req) {
    	
		log.debug(stringifyStacktrace(ex));
		
		ErrorMessage errorMessage = new ErrorMessage(ex.getMessage(), req.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR.value());
		
		return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    

    @ExceptionHandler({ MethodArgumentNotValidException.class })
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest req) {
    	
		log.debug(stringifyStacktrace(ex));
		
		Map<String, String> errors = new HashMap<>();
	    ex.getBindingResult().getAllErrors().forEach(error -> {
	        String fieldName = ((FieldError) error).getField();
	        String errorMessage = error.getDefaultMessage();
	        errors.put(fieldName, errorMessage);
	    });
		
		ErrorMessage errorMessage = new ErrorMessage("Validation failure. Check your inputs", errors, req.getRequestURI(), HttpStatus.BAD_REQUEST.value());
		
		return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
    
    private String stringifyStacktrace (Throwable err) {
    	StringWriter sw = new StringWriter();
    	PrintWriter pw = new PrintWriter(sw);
    	err.printStackTrace(pw);
    	return sw.toString();
    }

}
