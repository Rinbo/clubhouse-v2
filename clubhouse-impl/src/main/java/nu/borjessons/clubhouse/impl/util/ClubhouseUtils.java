package nu.borjessons.clubhouse.impl.util;

import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClubhouseUtils {
  public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public static Supplier<NoSuchElementException> createNotFoundExceptionSupplier(String message) {
    return () -> new NoSuchElementException(message);
  }
}
