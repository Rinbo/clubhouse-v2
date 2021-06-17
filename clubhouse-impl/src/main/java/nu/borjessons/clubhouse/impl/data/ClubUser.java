package nu.borjessons.clubhouse.impl.data;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
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
  @GeneratedValue
  private long id;

  @ManyToOne
  private Club club;

  @ManyToOne(cascade = CascadeType.PERSIST)
  private User user;

  @ManyToMany(fetch = FetchType.EAGER)
  private Set<RoleEntity> roles;

  public void addRoleEntity(RoleEntity roleEntity) {
    roles.add(roleEntity);
  }
}
