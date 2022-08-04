package nu.borjessons.clubhouse.impl.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import nu.borjessons.clubhouse.impl.data.key.UserId;

@Getter
@Setter
@Entity
@Table(name = "club", indexes = @Index(name = "ix_club_id", columnList = "clubId"))
public class Club extends BaseEntity {
  @OneToMany(mappedBy = "club", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Collection<Announcement> announcements;

  @Setter(AccessLevel.PRIVATE)
  @Column(nullable = false, length = 120, unique = true)
  private String clubId;

  @OneToMany(mappedBy = "club", orphanRemoval = true, fetch = FetchType.LAZY)
  private Collection<ClubUser> clubUsers = new ArrayList<>();

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
  private ImageToken logo;

  @Column(nullable = false, length = 120, unique = true)
  private String name;

  @Column(nullable = false, length = 120, unique = true)
  private String path;

  @Column(columnDefinition = "VARCHAR(16)")
  private String primaryColor;

  @Column(columnDefinition = "VARCHAR(16)")
  private String secondaryColor;

  @OneToMany(
      mappedBy = "club",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private Set<Team> teams = new HashSet<>();

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Type type;

  public Club(String clubId, String name, String path, Type type) {
    this.clubId = clubId;
    this.name = name;
    this.path = path;
    this.type = type;
  }

  public Club() {
    clubId = UUID.randomUUID().toString();
  }

  public void addClubUser(ClubUser clubUser) {
    clubUsers.add(clubUser);
    clubUser.setClub(this);
  }

  public void addTeam(Team team) {
    teams.add(team);
    team.setClub(this);
  }

  public Optional<ClubUser> getClubUser(UserId userId) {
    return clubUsers.stream()
        .filter(clubUser -> clubUser.getUser().getUserId()
            .equals(userId)).findFirst();
  }

  public List<ClubUser> getClubUsers(List<UserId> userIds) {
    return clubUsers.stream()
        .filter(clubUser -> userIds.contains(clubUser.getUser().getUserId()))
        .toList();
  }

  public Optional<Team> getTeamByTeamId(String teamId) {
    return getTeams().stream().filter(team -> team.getTeamId().equals(teamId)).findFirst();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((clubId == null) ? 0 : clubId.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Club other = (Club) obj;
    if (clubId == null) {
      return other.clubId == null;
    } else
      return clubId.equals(other.clubId);
  }

  public enum Type {
    SPORT,
    MUSIC,
    MISC
  }
}
