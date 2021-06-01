package nu.borjessons.clubhouse.impl.data;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "club")
public class Club extends BaseEntity {
  private static final long serialVersionUID = -5573907533182487531L;

  @Column(nullable = false, unique = true)
  private String clubId = UUID.randomUUID().toString();

  @OneToMany(mappedBy = "club", orphanRemoval = true, fetch = FetchType.EAGER)
  private Set<ClubRole> clubRoles = new HashSet<>();

  @Id
  @GeneratedValue
  private long id;

  @Column(nullable = false, length = 120, unique = true)
  private String name;

  @Column(nullable = false, length = 120, unique = true)
  private String path;

  @OneToMany(
      mappedBy = "club",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.EAGER)
  private Set<Team> teams = new HashSet<>();

  @Column(nullable = false)
  private Type type;

  public Club(String name, Type type) {
    this.name = name;
    this.path = name.toLowerCase().replace(" ", "-");
    this.type = type;
  }

  public void addClubRole(ClubRole clubRole) {
    this.clubRoles.add(clubRole);
  }

  public void addTeam(Team team) {
    teams.add(team);
    team.setClub(this);
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

  public Set<User> getManagedUsers() {
    return getUsers().stream()
        .map(User::getChildren)
        .flatMap(Set::stream)
        .collect(Collectors.toSet());
  }

  public Optional<User> getUser(String userId) {
    return getUsers()
        .stream()
        .filter(user -> user.getUserId().equals(userId))
        .findFirst();
  }

  public Set<User> getUsers() {
    return clubRoles.stream()
        .map(ClubRole::getUser)
        .map(user -> {
          Set<User> combinedSet = new HashSet<>(user.getChildren());
          combinedSet.add(user);
          return combinedSet;
        })
        .flatMap(Set::stream)
        .collect(Collectors.toSet());
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((clubId == null) ? 0 : clubId.hashCode());
    return result;
  }

  public void removeClubRole(ClubRole clubRole) {
    this.clubRoles.remove(clubRole);
  }

  public enum Type {
    SPORT,
    MUSIC,
    MISC
  }
}
