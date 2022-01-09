package nu.borjessons.clubhouse.impl.dto.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import nu.borjessons.clubhouse.impl.data.key.ImageTokenId;

public class ImageIdSerializer extends JsonSerializer<ImageTokenId> {
  public static final JsonSerializer<ImageTokenId> INSTANCE = new ImageIdSerializer();

  private ImageIdSerializer() {
    // do nothing
  }

  @Override
  public void serialize(ImageTokenId userId, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
    jsonGenerator.writeStartObject();
    jsonGenerator.writeStringField("imageId", userId.toString());
    jsonGenerator.writeEndObject();
  }
}
