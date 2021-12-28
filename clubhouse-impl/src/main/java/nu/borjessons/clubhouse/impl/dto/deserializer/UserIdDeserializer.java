package nu.borjessons.clubhouse.impl.dto.deserializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import nu.borjessons.clubhouse.impl.data.key.UserId;

public class UserIdDeserializer extends JsonDeserializer<UserId> {
  public static final JsonDeserializer<UserId> INSTANCE = new UserIdDeserializer();

  private UserIdDeserializer() {
    // do nothing
  }

  @Override
  public UserId deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    JsonNode jsonNode = jsonParser.getCodec().readTree(jsonParser);
    return new UserId(jsonNode.get("userId").asText());
  }
}
