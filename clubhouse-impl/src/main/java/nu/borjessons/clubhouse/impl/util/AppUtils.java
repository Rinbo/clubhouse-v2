package nu.borjessons.clubhouse.impl.util;

import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppUtils {
  public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public static Supplier<ResponseStatusException> createAccessDeniedSupplier() {
    return () -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
  }

  public static Supplier<NoSuchElementException> createNotFoundExceptionSupplier(String message) {
    return () -> new NoSuchElementException(message);
  }
}
