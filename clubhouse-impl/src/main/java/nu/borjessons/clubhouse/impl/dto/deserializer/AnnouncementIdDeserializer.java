package nu.borjessons.clubhouse.impl.dto.deserializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import nu.borjessons.clubhouse.impl.data.key.AnnouncementId;

public class AnnouncementIdDeserializer extends JsonDeserializer<AnnouncementId> {
  public static final JsonDeserializer<AnnouncementId> INSTANCE = new AnnouncementIdDeserializer();

  private AnnouncementIdDeserializer() {
    // do nothing
  }

  @Override
  public AnnouncementId deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
    return new AnnouncementId(jsonNode.asText());
  }
}
