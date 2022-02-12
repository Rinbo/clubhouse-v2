package nu.borjessons.clubhouse.impl.data.converter;

import javax.persistence.AttributeConverter;

import nu.borjessons.clubhouse.impl.data.key.ImageTokenId;

public class ImageTokenIdConverter implements AttributeConverter<ImageTokenId, String> {
  @Override
  public String convertToDatabaseColumn(ImageTokenId imageTokenId) {
    return imageTokenId.toString();
  }

  @Override
  public ImageTokenId convertToEntityAttribute(String string) {
    return new ImageTokenId(string);
  }
}


