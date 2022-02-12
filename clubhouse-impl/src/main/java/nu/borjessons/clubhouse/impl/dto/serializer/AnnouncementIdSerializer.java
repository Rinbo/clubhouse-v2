package nu.borjessons.clubhouse.impl.dto.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import nu.borjessons.clubhouse.impl.data.key.AnnouncementId;

public class AnnouncementIdSerializer extends JsonSerializer<AnnouncementId> {
  public static final JsonSerializer<AnnouncementId> INSTANCE = new AnnouncementIdSerializer();

  private AnnouncementIdSerializer() {
    // do nothing
  }

  @Override
  public void serialize(AnnouncementId announcementId, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
    jsonGenerator.writeString(announcementId.toString());
  }
}
