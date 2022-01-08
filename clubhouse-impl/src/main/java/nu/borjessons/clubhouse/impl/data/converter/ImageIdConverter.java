package nu.borjessons.clubhouse.impl.data.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import nu.borjessons.clubhouse.impl.data.key.ImageId;

@Converter(autoApply = true)
public class ImageIdConverter implements AttributeConverter<ImageId, String> {
  @Override
  public String convertToDatabaseColumn(ImageId imageId) {
    return imageId.toString();
  }

  @Override
  public ImageId convertToEntityAttribute(String string) {
    return new ImageId(string);
  }
}


