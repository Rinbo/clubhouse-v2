package nu.borjessons.clubhouse.impl.data.converter;

import javax.persistence.AttributeConverter;

import nu.borjessons.clubhouse.impl.data.key.AnnouncementId;

public class AnnouncementIdConverter implements AttributeConverter<AnnouncementId, String> {
  @Override
  public String convertToDatabaseColumn(AnnouncementId announcementId) {
    return announcementId.toString();
  }

  @Override
  public AnnouncementId convertToEntityAttribute(String string) {
    return new AnnouncementId(string);
  }
}


