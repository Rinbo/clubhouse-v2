package nu.borjessons.clubhouse.impl.dto;

import java.io.InputStream;

import nu.borjessons.clubhouse.impl.data.ImageToken;
import nu.borjessons.clubhouse.impl.util.Validate;

public record ImageStream(ImageToken imageToken, InputStream inputStream) {
  public ImageStream {
    Validate.notNull(imageToken, "imageToken");
    Validate.notNull(inputStream, "inputStream");
  }
}
