package nu.borjessons.clubhouse.impl.data.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import nu.borjessons.clubhouse.impl.data.key.UserId;

@Converter(autoApply = true)
public class UserIdConverter implements AttributeConverter<UserId, String> {
  @Override
  public String convertToDatabaseColumn(UserId userId) {
    return userId.toString();
  }

  @Override
  public UserId convertToEntityAttribute(String string) {
    return new UserId(string);
  }
}


