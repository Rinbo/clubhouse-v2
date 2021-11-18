package nu.borjessons.clubhouse.impl.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.Address;
import nu.borjessons.clubhouse.impl.data.ClubUser;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.ClubDTO;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.dto.rest.UpdateUserModel;
import nu.borjessons.clubhouse.impl.repository.AddressRepository;
import nu.borjessons.clubhouse.impl.repository.UserRepository;
import nu.borjessons.clubhouse.impl.service.UserService;
import nu.borjessons.clubhouse.impl.util.ClubhouseMappers;
import nu.borjessons.clubhouse.impl.util.ClubhouseUtils;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final AddressRepository addressRepository;
  private final ClubhouseMappers clubhouseMappers;
  private final UserRepository userRepository;

  @Override
  public UserDTO createUser(User user) {
    return UserDTO.create(userRepository.save(user));
  }

  @Override
  public List<UserDTO> createUsers(List<User> users) {
    return userRepository.saveAll(users)
        .stream()
        .map(UserDTO::create)
        .collect(Collectors.toList());
  }

  @Override
  public UserDTO getUserByUserName(String username) {
    return UserDTO.create(getUserByEmail(username));
  }

  @Override
  public UserDTO getById(long id) {
    return UserDTO.create(getUser(id));
  }

  @Override
  @Transactional
  public void deleteUser(long id) {
    User user = getUser(id);
    Set<User> children = Set.copyOf(user.getChildren());
    children.forEach(child -> updateOrDeleteChild(child, user));
    userRepository.delete(user);
  }

  @Override
  public User getUserByEmail(String email) {
    return userRepository.findByEmail(email).orElseThrow();
  }

  @Override
  @Transactional
  public UserDTO updateUser(long id, UpdateUserModel userDetails) {
    User user = getUser(id);
    user.setFirstName(userDetails.getFirstName());
    user.setLastName(userDetails.getLastName());
    user.setDateOfBirth(LocalDate.parse(userDetails.getDateOfBirth(), ClubhouseUtils.DATE_FORMAT));

    Set<Address> addresses = clubhouseMappers.addressModelToAddress(userDetails.getAddresses());
    Set<Address> oldAddresses = user.getAddresses();
    oldAddresses.forEach(user::removeAddress);
    addressRepository.deleteAll(oldAddresses);
    addresses.forEach(user::addAddress);

    return UserDTO.create(userRepository.save(user));
  }

  @Override
  public void updateUserLoginTime(String email) {
    User user = userRepository.findByEmail(email).orElseThrow();
    user.setLastLoginTime(LocalDateTime.now());
    userRepository.save(user);
  }

  @Override
  public List<ClubDTO> getMyClubs(String userId) {
    User user = getUser(userId);
    return user.getClubUsers()
        .stream()
        .map(ClubUser::getClub)
        .map(ClubDTO::new)
        .toList();
  }

  @Transactional
  @Override
  public UserDTO updateChild(String childId, String parentId, UpdateUserModel userDetails) {
    User parent = userRepository.findByUserId(parentId).orElseThrow();
    User child = parent.getChildren().stream().filter(c -> c.getUserId().equals(childId)).findFirst().orElseThrow();
    child.setFirstName(userDetails.getFirstName());
    child.setLastName(userDetails.getLastName());
    child.setDateOfBirth(LocalDate.parse(userDetails.getDateOfBirth(), ClubhouseUtils.DATE_FORMAT));
    return UserDTO.create(userRepository.save(child));
  }

  @Override
  public UserDetails loadUserByUsername(String username) {
    return userRepository.findByEmail(username).orElseThrow();
  }

  private void updateOrDeleteChild(User child, User parent) {
    child.removeParent(parent);
    if (child.getParents().isEmpty()) {
      userRepository.delete(child);
    } else {
      userRepository.save(child);
    }
  }

  private User getUser(long id) {
    return userRepository.findById(id).orElseThrow();
  }

  private User getUser(String userId) {
    return userRepository.findByUserId(userId).orElseThrow();
  }
}
