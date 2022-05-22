package nu.borjessons.clubhouse.impl.data.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import nu.borjessons.clubhouse.impl.data.key.TeamPostId;

@Converter
public class TeamPostIdConverter implements AttributeConverter<TeamPostId, String> {
  @Override
  public String convertToDatabaseColumn(TeamPostId teamPostId) {
    return teamPostId.toString();
  }

  @Override
  public TeamPostId convertToEntityAttribute(String string) {
    return new TeamPostId(string);
  }
}


