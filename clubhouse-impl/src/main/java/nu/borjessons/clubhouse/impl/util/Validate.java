package nu.borjessons.clubhouse.impl.util;

import java.util.Locale;

public class Validate {
  public static void isPositive(long value, String parameterName) {
    if (value < 1) throw new IllegalArgumentException(String.format(Locale.ROOT, "%s must be positive", parameterName));
  }

  public static void notEmpty(String string, String parameterName) {
    notNull(string, parameterName);
    if (string.isEmpty() || string.isBlank()) throw new IllegalArgumentException(String.format(Locale.ROOT, "%s must not be blank", parameterName));
  }

  public static void notNull(Object object, String parameterName) {
    if (object == null) throw new IllegalArgumentException(String.format(Locale.ROOT, "%s must not be null", parameterName));
  }

  private Validate() {
    throw new IllegalStateException("Utility class");
  }
}
