package nu.borjessons.clubhouse.impl.util;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.Address;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.rest.AddressModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateChildRequestModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateClubModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateUserModel;

@Component
@RequiredArgsConstructor
public class ClubhouseMappers {
  private static final String EMAIL_EXTENSION = "@clubhouse.nu";
  private final PasswordEncoder passwordEncoder;

  public Set<Address> addressModelToAddress(List<AddressModel> addressModels) {
    return addressModels.stream().map(this::addressOf).collect(Collectors.toSet());
  }

  public User childCreationModelToUser(CreateChildRequestModel childModel) {
    User child = new User(UUID.randomUUID().toString());
    String childIdentifier = child.getUserId();
    child.setFirstName(childModel.getFirstName());
    child.setLastName(childModel.getLastName());
    child.setEmail(childIdentifier + EMAIL_EXTENSION);
    child.setEncryptedPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
    child.setDateOfBirth(LocalDate.parse(childModel.getDateOfBirth(), ClubhouseUtils.DATE_FORMAT));
    child.setManagedAccount(true);
    return child;
  }

  public Club clubCreationModelToClub(CreateClubModel clubDetails) {
    return new Club(clubDetails.getName(), clubDetails.getType(), UUID.randomUUID().toString());
  }

  public Club clubCreationModelToClub(CreateClubModel clubDetails, String clubId) {
    return new Club(clubDetails.getName(), clubDetails.getType(), clubId);
  }

  public User userCreationModelToUser(CreateUserModel userDetails) {
    return userCreationModelToUser(userDetails, UUID.randomUUID().toString());
  }

  public User userCreationModelToUser(CreateUserModel userDetails, String userId) {
    User user = new User(userId);
    user.setEmail(userDetails.getEmail().toLowerCase().trim());
    user.setFirstName(userDetails.getFirstName());
    user.setLastName(userDetails.getLastName());
    user.setEncryptedPassword(passwordEncoder.encode(userDetails.getPassword()));
    user.setDateOfBirth(LocalDate.parse(userDetails.getDateOfBirth(), ClubhouseUtils.DATE_FORMAT));

    return user;
  }

  private Address addressOf(AddressModel addressModel) {
    Address address = new Address();
    address.setStreet(addressModel.getStreet());
    address.setPostalCode(addressModel.getPostalCode());
    address.setCity(addressModel.getCity());
    address.setCountry(addressModel.getCountry());
    return address;
  }
}
