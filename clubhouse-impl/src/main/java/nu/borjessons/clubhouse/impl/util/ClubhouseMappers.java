package nu.borjessons.clubhouse.impl.util;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.controller.model.request.AddressModel;
import nu.borjessons.clubhouse.impl.controller.model.request.CreateChildRequestModel;
import nu.borjessons.clubhouse.impl.controller.model.request.CreateClubModel;
import nu.borjessons.clubhouse.impl.controller.model.request.CreateUserModel;
import nu.borjessons.clubhouse.impl.data.Address;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.ClubRole;
import nu.borjessons.clubhouse.impl.data.ClubRole.Role;
import nu.borjessons.clubhouse.impl.data.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ClubhouseMappers {

  private static final String EMAIL_EXTENSION = "@clubhouse.nu";
  private final BCryptPasswordEncoder bcryptPasswordEncoder;

  public Club clubCreationModelToClub(CreateClubModel clubDetails) {
    return new Club(clubDetails.getName(), clubDetails.getType());
  }

  public User userCreationModelToUser(CreateUserModel userDetails) {
    User user = new User();
    user.setEmail(userDetails.getEmail().toLowerCase().trim());
    user.setFirstName(userDetails.getFirstName());
    user.setLastName(userDetails.getLastName());
    user.setEncryptedPassword(bcryptPasswordEncoder.encode(userDetails.getPassword()));
    user.setDateOfBirth(LocalDate.parse(userDetails.getDateOfBirth(), ClubhouseUtils.DATE_FORMAT));

    return user;
  }

  public Set<ClubRole> mapClubRoles(Set<Role> roles, User user, Club club) {
    return roles.stream().map(role -> new ClubRole(role, user, club)).collect(Collectors.toSet());
  }

  public User childCreationModelToUser(CreateChildRequestModel childModel) {
    User child = new User();
    String childIdentifier = child.getUserId();
    child.setFirstName(childModel.getFirstName());
    child.setLastName(childModel.getLastName());
    child.setEmail(childIdentifier + EMAIL_EXTENSION);
    child.setEncryptedPassword(bcryptPasswordEncoder.encode(UUID.randomUUID().toString()));
    child.setDateOfBirth(LocalDate.parse(childModel.getDateOfBirth(), ClubhouseUtils.DATE_FORMAT));
    child.setManagedAccount(true);
    return child;
  }

  public Set<Address> addressModelToAddress(Set<AddressModel> addressModels) {
    return addressModels.stream().map(this::addressOf).collect(Collectors.toSet());
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
