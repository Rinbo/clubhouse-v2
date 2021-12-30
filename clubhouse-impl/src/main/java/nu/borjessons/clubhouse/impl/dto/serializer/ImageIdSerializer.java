package nu.borjessons.clubhouse.impl.dto.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import nu.borjessons.clubhouse.impl.data.key.ImageId;

public class ImageIdSerializer extends JsonSerializer<ImageId> {
  public static final JsonSerializer<ImageId> INSTANCE = new ImageIdSerializer();

  private ImageIdSerializer() {
    // do nothing
  }

  @Override
  public void serialize(ImageId userId, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
    jsonGenerator.writeStartObject();
    jsonGenerator.writeStringField("imageId", userId.toString());
    jsonGenerator.writeEndObject();
  }
}
