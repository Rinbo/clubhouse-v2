package nu.borjessons.clubhouse.impl.data.key;

import java.util.Objects;

abstract class BaseId {
  private final String string;

  BaseId(String string) {
    Objects.requireNonNull(string, "string must not be null");

    this.string = string;
  }

  @Override
  public int hashCode() {
    return string.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || getClass() != object.getClass()) return false;

    BaseId baseId = (BaseId) object;

    return string.equals(baseId.string);
  }

  @Override
  public String toString() {
    return string;
  }
}
