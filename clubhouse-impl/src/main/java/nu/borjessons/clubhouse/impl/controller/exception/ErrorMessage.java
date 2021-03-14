package nu.borjessons.clubhouse.impl.controller.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class ErrorMessage {

  private static final long serialVersionUID = 1L;

  private LocalDateTime timestamp = LocalDateTime.now();
  private Map<String, String> errors;
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

  public ErrorMessage(String message, Map<String, String> errors, String path, int status) {
    this.message = message;
    this.errors = errors;
    this.path = path;
    this.status = status;
  }
}
