package nu.borjessons.clubhouse.impl.dto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public final class ErrorMessage {
  private final Map<String, String> errors = new HashMap<>();
  private final String message;
  private final String path;
  private final int status;
  private final LocalDateTime timestamp = LocalDateTime.now();

  public ErrorMessage(String message, String path, int status) {
    this.message = message;
    this.path = path;
    this.status = status;
  }

  public ErrorMessage(String message, Map<String, String> errors, String path, int status) {
    this.message = message;
    this.errors.putAll(errors);
    this.path = path;
    this.status = status;
  }
}
