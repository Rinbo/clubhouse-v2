package nu.borjessons.clubhouse.impl.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.Setter;
import nu.borjessons.clubhouse.impl.dto.Role;

@Getter
@Setter
@Entity
@Table(name = "club_user", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "club_id"}))
public class ClubUser extends BaseEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  private Club club;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToMany(fetch = FetchType.LAZY)
  private List<Team> managedTeams = new ArrayList<>();

  @ManyToMany(fetch = FetchType.EAGER)
  private Set<RoleEntity> roles = new HashSet<>();
  
  @ManyToMany(fetch = FetchType.LAZY)
  private List<Team> teams = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  private User user;

  public void addRoleEntity(RoleEntity roleEntity) {
    roles.add(roleEntity);
  }

  public List<Team> getJoinedTeams() {
    return Stream.concat(teams.stream(), this.managedTeams.stream()).toList();
  }

  public void removeParentRole() {
    roles.removeIf(roleEntity -> roleEntity.getName() == Role.PARENT);
  }
}
