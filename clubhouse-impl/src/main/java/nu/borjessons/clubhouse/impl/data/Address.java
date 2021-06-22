package nu.borjessons.clubhouse.impl.data;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Address extends BaseEntity {
  private static final long serialVersionUID = 8296297244892527350L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  
  @Column(nullable = false, unique = true)
  private final String addressId = UUID.randomUUID().toString();

  @Column(length = 30, nullable = false)
  private String city;

  @Column(length = 30, nullable = false)
  private String country;

  @Column(length = 10, nullable = false)
  private String postalCode;

  @Column(length = 100, nullable = false)
  private String street;

  @ManyToOne
  private User user;

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((addressId == null) ? 0 : addressId.hashCode());
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
    Address other = (Address) obj;
    if (addressId == null) {
      return other.addressId == null;
    } else
      return addressId.equals(other.addressId);
  }
}
