package nu.borjessons.clubhouse.impl.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nu.borjessons.clubhouse.impl.data.Club;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClubDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private String clubId;
  private String name;
  private String path;
  private Club.Type type;

  public ClubDTO(Club club) {
    this.clubId = club.getClubId();
    this.name = club.getName();
    this.path = club.getPath();
    this.type = club.getType();
  }
}
