package nu.borjessons.clubhouse.impl.dto.deserializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import nu.borjessons.clubhouse.impl.data.key.TeamPostId;

public class TeamPostIdDeserializer extends JsonDeserializer<TeamPostId> {
  public static final JsonDeserializer<TeamPostId> INSTANCE = new TeamPostIdDeserializer();

  private TeamPostIdDeserializer() {
    // do nothing
  }

  @Override
  public TeamPostId deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
    return new TeamPostId(jsonNode.asText());
  }
}
