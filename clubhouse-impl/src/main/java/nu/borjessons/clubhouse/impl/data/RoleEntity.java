package nu.borjessons.clubhouse.impl.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "role")
public class RoleEntity implements Serializable {
  private static final long serialVersionUID = 8384245478087805632L;

  @Id
  @GeneratedValue
  private long id;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Role name;
}
