package nu.borjessons.clubhouse.impl.dto.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import nu.borjessons.clubhouse.impl.data.key.TeamPostId;

public class TeamPostIdSerializer extends JsonSerializer<TeamPostId> {
  public static final JsonSerializer<TeamPostId> INSTANCE = new TeamPostIdSerializer();

  private TeamPostIdSerializer() {
    // do nothing
  }

  @Override
  public void serialize(TeamPostId teamPostId, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
    jsonGenerator.writeString(teamPostId.toString());
  }
}
