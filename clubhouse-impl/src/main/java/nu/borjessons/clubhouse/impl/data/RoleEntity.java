package nu.borjessons.clubhouse.impl.data;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import nu.borjessons.clubhouse.impl.dto.Role;

@Setter
@Getter
@Entity
@Table(name = "role")
public class RoleEntity implements Serializable {
  private static final long serialVersionUID = 8384245478087805632L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Role name;

  @Override public int hashCode() {
    return Objects.hash(name);
  }

  @Override public boolean equals(Object other) {
    if (this == other) return true;
    if (other == null || getClass() != other.getClass()) return false;
    RoleEntity otherRoleEntity = (RoleEntity) other;
    return name == otherRoleEntity.name;
  }
}
