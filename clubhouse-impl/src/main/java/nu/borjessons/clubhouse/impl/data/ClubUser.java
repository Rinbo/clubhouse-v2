package nu.borjessons.clubhouse.impl.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

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

@Getter
@Setter
@Entity
@Table(name = "club_user", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "club_id"}))
public class ClubUser extends BaseEntity implements Serializable {
  private static final long serialVersionUID = 1429757107786078376L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private final String clubUserId = UUID.randomUUID().toString();

  @ManyToOne(fetch = FetchType.LAZY)
  private Club club;

  @ManyToOne(fetch = FetchType.LAZY)
  private User user;

  @ManyToMany(mappedBy = "leaders", fetch = FetchType.LAZY)
  private Collection<Team> managedTeams = new ArrayList<>();

  @ManyToMany(mappedBy = "members", fetch = FetchType.LAZY)
  private Collection<Team> teams = new ArrayList<>();

  @ManyToMany(fetch = FetchType.EAGER)
  private Set<RoleEntity> roles;

  public void addRoleEntity(RoleEntity roleEntity) {
    roles.add(roleEntity);
  }

  public void doOrphan() {
    club.removeClubUser(this);
    user.removeClubUser(this);
  }
}
