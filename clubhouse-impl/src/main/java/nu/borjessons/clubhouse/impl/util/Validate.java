package nu.borjessons.clubhouse.impl.util;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class Validate {
  private static final Pattern COLOR_PATTERN = Pattern.compile("[#][0-9A-F]{6}", Pattern.CASE_INSENSITIVE);

  public static void isPositive(long value, String parameterName) {
    if (value < 1) throw new IllegalArgumentException(String.format(Locale.ROOT, "%s must be positive", parameterName));
  }

  public static void notEmpty(String string, String parameterName) {
    notNull(string, parameterName);
    if (string.isEmpty() || string.isBlank()) throw new IllegalArgumentException(String.format(Locale.ROOT, "%s must not be blank", parameterName));
  }

  public static <T> void notEmpty(List<T> list, String parameterName) {
    notNull(list, parameterName);
    if (list.isEmpty()) throw new IllegalArgumentException(String.format(Locale.ROOT, "%s must not be empty", parameterName));
  }

  public static void notNegative(long value, String parameterName) {
    if (value < 0) throw new IllegalArgumentException(String.format(Locale.ROOT, "%s must not be negative", parameterName));
  }

  public static void notNull(Object object, String parameterName) {
    if (object == null) throw new IllegalArgumentException(String.format(Locale.ROOT, "%s must not be null", parameterName));
  }

  public static void validateIsColorOrNull(String color, String parameterName) {
    if (color == null) return;
    if (!COLOR_PATTERN.matcher(color).matches()) throw new IllegalArgumentException(String.format(Locale.ROOT, "%s is not an RGB color", parameterName));
  }

  private Validate() {
    throw new IllegalStateException("Utility class");
  }
}
