package nu.borjessons.clubhouse.impl.data;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import nu.borjessons.clubhouse.impl.data.key.AddressId;

@Entity
@Getter
@Setter
public class Image extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Setter(AccessLevel.PRIVATE)
  @Column(nullable = false, unique = true, columnDefinition = "varchar(255)")
  private AddressId addressId = new AddressId(UUID.randomUUID().toString());

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String contentType;
}
