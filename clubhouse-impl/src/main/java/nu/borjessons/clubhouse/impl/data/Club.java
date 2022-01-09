package nu.borjessons.clubhouse.impl.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import nu.borjessons.clubhouse.impl.data.key.ImageTokenId;
import nu.borjessons.clubhouse.impl.data.key.UserId;

@Getter
@Setter
@Entity
@Table(name = "club", indexes = @Index(name = "ix_club_id", columnList = "clubId"))
public class Club extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Setter(AccessLevel.PRIVATE)
  @Column(nullable = false, length = 120, unique = true)
  private String clubId;

  @OneToMany(mappedBy = "club", orphanRemoval = true, fetch = FetchType.LAZY)
  private Collection<ClubUser> clubUsers = new ArrayList<>();

  @Column(nullable = false, length = 120, unique = true)
  private String name;

  @Column(nullable = false, length = 120, unique = true)
  private String path;

  @Column(columnDefinition = "varchar(255)")
  private ImageTokenId logoId;

  @OneToMany(
      mappedBy = "club",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private Set<Team> teams = new HashSet<>();

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Type type;

  public Club(String name, Type type, String clubId) {
    this.name = name;
    this.path = name.toLowerCase().replace(" ", "-");
    this.type = type;
    this.clubId = clubId;
  }

  public Club() {
    clubId = UUID.randomUUID().toString();
  }

  public void addTeam(Team team) {
    teams.add(team);
    team.setClub(this);
  }

  public Set<User> getManagedUsers() {
    return clubUsers.stream()
        .map(ClubUser::getUser)
        .filter(User::isManagedAccount)
        .collect(Collectors.toSet());
  }

  public List<ClubUser> getClubUsers(List<UserId> userIds) {
    return clubUsers.stream()
        .filter(clubUser -> userIds.contains(clubUser.getUser().getUserId()))
        .toList();
  }

  public Optional<ClubUser> getClubUser(UserId userId) {
    return clubUsers.stream()
        .filter(clubUser -> clubUser.getUser().getUserId()
            .equals(userId)).findFirst();
  }

  public Optional<Team> getTeamByTeamId(String teamId) {
    return getTeams().stream().filter(team -> team.getTeamId().equals(teamId)).findFirst();
  }

  public void removeClubUser(ClubUser clubUser) {
    clubUsers.remove(clubUser);
    clubUser.setClub(null);
  }

  public void addClubUser(ClubUser clubUser) {
    clubUsers.add(clubUser);
    clubUser.setClub(this);
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
