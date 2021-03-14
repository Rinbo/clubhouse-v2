package nu.borjessons.clubhouse.impl.dto;

import lombok.Getter;
import lombok.Setter;
import nu.borjessons.clubhouse.impl.data.Address;

import java.io.Serializable;

@Getter
@Setter
public class AddressDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private String addressId;
  private String city;
  private String country;
  private String street;
  private String postalCode;

  public AddressDTO(Address address) {
    addressId = address.getAddressId();
    street = address.getStreet();
    postalCode = address.getPostalCode();
    city = address.getCity();
    country = address.getCountry();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((city == null) ? 0 : city.hashCode());
    result = prime * result + ((postalCode == null) ? 0 : postalCode.hashCode());
    result = prime * result + ((street == null) ? 0 : street.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    AddressDTO other = (AddressDTO) obj;
    if (city == null) {
      if (other.city != null) return false;
    } else if (!city.equals(other.city)) return false;
    if (postalCode == null) {
      if (other.postalCode != null) return false;
    } else if (!postalCode.equals(other.postalCode)) return false;
    if (street == null) {
      if (other.street != null) return false;
    } else if (!street.equals(other.street)) return false;
    return true;
  }
}
