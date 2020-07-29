package nu.borjessons.clubhouse.controller.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    
    private String stringifyStacktrace (Throwable err) {
    	StringWriter sw = new StringWriter();
    	PrintWriter pw = new PrintWriter(sw);
    	err.printStackTrace(pw);
    	return sw.toString();
    }

}
