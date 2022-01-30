package nu.borjessons.clubhouse.impl.dto.deserializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import nu.borjessons.clubhouse.impl.data.key.ImageTokenId;

public class ImageTokenIdDeserializer extends JsonDeserializer<ImageTokenId> {
  public static final JsonDeserializer<ImageTokenId> INSTANCE = new ImageTokenIdDeserializer();

  private ImageTokenIdDeserializer() {
    // do nothing
  }

  @Override
  public ImageTokenId deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
    return new ImageTokenId(jsonNode.get("imageTokenId").asText());
  }
}
