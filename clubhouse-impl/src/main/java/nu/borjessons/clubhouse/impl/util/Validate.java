package nu.borjessons.clubhouse.impl.util;

import java.util.Locale;

public class Validate {
  public static void notNull(Object object, String parameterName) {
    if (object == null) throw new IllegalArgumentException(String.format(Locale.ROOT, "%s must not be null", parameterName));
  }

  public static void notEmpty(String string, String parameterName) {
    if (string.isEmpty() || string.isBlank()) throw new IllegalArgumentException(String.format(Locale.ROOT, "%s must not be null", parameterName));
  }
}
