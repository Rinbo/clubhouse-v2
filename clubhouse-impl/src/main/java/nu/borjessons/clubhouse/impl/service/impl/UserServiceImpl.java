package nu.borjessons.clubhouse.impl.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.Address;
import nu.borjessons.clubhouse.impl.data.ClubUser;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.dto.BaseUserRecord;
import nu.borjessons.clubhouse.impl.dto.ClubRecord;
import nu.borjessons.clubhouse.impl.dto.UserDto;
import nu.borjessons.clubhouse.impl.dto.rest.UpdateUserModel;
import nu.borjessons.clubhouse.impl.repository.AddressRepository;
import nu.borjessons.clubhouse.impl.repository.UserRepository;
import nu.borjessons.clubhouse.impl.service.UserService;
import nu.borjessons.clubhouse.impl.util.AppMappers;
import nu.borjessons.clubhouse.impl.util.AppUtils;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final AddressRepository addressRepository;
  private final AppMappers appMappers;
  private final UserRepository userRepository;

  @Override
  public void addParentToChild(UserId originalParentId, UserId childId, UserId newParentId) {
    User originalParent = userRepository.findByUserId(originalParentId).orElseThrow();
    User child = originalParent.getChildren().stream().filter(c -> c.getUserId().equals(childId)).findFirst().orElseThrow();
    User newParent = userRepository.findByUserId(newParentId).orElseThrow();
    newParent.addChild(child);
    userRepository.save(newParent);
  }

  @Override
  public UserDto createUser(User user) {
    return UserDto.create(userRepository.save(user));
  }

  @Override
  public List<UserDto> createUsers(List<User> users) {
    return userRepository.saveAll(users)
        .stream()
        .map(UserDto::create)
        .toList();
  }

  @Override
  @Transactional
  public void deleteUser(long id) {
    User user = getUser(id);
    Set<User> children = Set.copyOf(user.getChildren());
    children.forEach(child -> updateOrDeleteChild(child, user));
    removeForeignKeyReferences(user);
    userRepository.delete(user);
  }

  @Override
  public UserDto getById(long id) {
    return UserDto.create(getUser(id));
  }

  @Override
  public Collection<BaseUserRecord> getChildren(long id) {
    return userRepository
        .getById(id)
        .getChildren()
        .stream()
        .sorted(Comparator.comparing(User::getFirstName))
        .map(BaseUserRecord::new)
        .toList();
  }

  @Override
  public List<ClubRecord> getMyClubs(UserId userId) {
    User user = getUser(userId);
    return user.getClubUsers()
        .stream()
        .map(ClubUser::getClub)
        .map(ClubRecord::new)
        .toList();
  }

  @Override
  public User getUserByEmail(String email) {
    return userRepository.findByEmail(email).orElseThrow();
  }

  @Override
  public UserDto getUserByUserName(String username) {
    return UserDto.create(getUserByEmail(username));
  }

  @Transactional
  @Override
  public UserDto updateChild(UserId childId, UserId parentId, UpdateUserModel userDetails) {
    User parent = userRepository.findByUserId(parentId).orElseThrow();
    User child = parent.getChildren().stream().filter(c -> c.getUserId().equals(childId)).findFirst().orElseThrow();
    child.setFirstName(userDetails.getFirstName());
    child.setLastName(userDetails.getLastName());
    child.setDateOfBirth(LocalDate.parse(userDetails.getDateOfBirth(), AppUtils.DATE_FORMAT));
    return UserDto.create(userRepository.save(child));
  }

  @Override
  @Transactional
  public UserDto updateUser(long id, UpdateUserModel userDetails) {
    User user = getUser(id);
    user.setFirstName(userDetails.getFirstName());
    user.setLastName(userDetails.getLastName());
    user.setDateOfBirth(LocalDate.parse(userDetails.getDateOfBirth(), AppUtils.DATE_FORMAT));
    user.setShowEmail(userDetails.isShowEmail());

    Set<Address> addresses = appMappers.addressModelToAddress(userDetails.getAddresses());
    Set<Address> oldAddresses = user.getAddresses();
    oldAddresses.forEach(user::removeAddress);
    addressRepository.deleteAll(oldAddresses);
    addresses.forEach(user::addAddress);

    return UserDto.create(userRepository.save(user));
  }

  @Override
  @Transactional
  public UserDto updateUserLoginTime(String email) {
    User user = userRepository.findByEmail(email).orElseThrow();
    user.setLastLoginTime(LocalDateTime.now());
    return UserDto.create(userRepository.save(user));
  }

  @Override
  public UserDetails loadUserByUsername(String username) {
    return userRepository.findByEmail(username).orElseThrow();
  }

  private User getUser(long id) {
    return userRepository.findById(id).orElseThrow();
  }

  private User getUser(UserId userId) {
    return userRepository.findByUserId(userId).orElseThrow();
  }

  private void removeForeignKeyReferences(User user) {
    userRepository.removeUserAnnouncementReferences(user.getId());
  }

  private void updateOrDeleteChild(User child, User parent) {
    child.removeParent(parent);
    if (child.getParents().isEmpty()) {
      removeForeignKeyReferences(child);
      userRepository.delete(child);
    } else {
      userRepository.save(child);
    }
  }
}
