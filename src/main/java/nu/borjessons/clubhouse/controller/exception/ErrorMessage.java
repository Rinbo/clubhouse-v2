package nu.borjessons.clubhouse.controller.exception;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	private LocalDateTime timestamp = LocalDateTime.now();
	private String message;
	private String path = "";
	private int status;
	
	public ErrorMessage(String message) {
		this.message = message;
	}
	
	public ErrorMessage(String message, String path, int status) {
		this.message = message;
		this.path = path;
		this.status = status;
	}
}
