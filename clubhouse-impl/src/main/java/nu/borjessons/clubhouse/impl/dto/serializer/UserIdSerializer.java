package nu.borjessons.clubhouse.impl.dto.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import nu.borjessons.clubhouse.impl.data.key.UserId;

public class UserIdSerializer extends JsonSerializer<UserId> {
  public static final JsonSerializer<UserId> INSTANCE = new UserIdSerializer();

  private UserIdSerializer() {
    // do nothing
  }

  @Override
  public void serialize(UserId userId, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
    jsonGenerator.writeStartObject();
    jsonGenerator.writeStringField("userId", userId.toString());
    jsonGenerator.writeEndObject();
  }
}
