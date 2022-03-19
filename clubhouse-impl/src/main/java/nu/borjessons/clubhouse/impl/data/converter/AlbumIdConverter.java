package nu.borjessons.clubhouse.impl.data.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import nu.borjessons.clubhouse.impl.data.key.AlbumId;

@Converter
public class AlbumIdConverter implements AttributeConverter<AlbumId, String> {
  @Override
  public String convertToDatabaseColumn(AlbumId albumId) {
    return albumId.toString();
  }

  @Override
  public AlbumId convertToEntityAttribute(String string) {
    return new AlbumId(string);
  }
}


