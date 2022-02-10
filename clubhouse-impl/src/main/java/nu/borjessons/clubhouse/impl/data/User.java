package nu.borjessons.clubhouse.impl.data;

import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import nu.borjessons.clubhouse.impl.data.converter.UserIdConverter;
import nu.borjessons.clubhouse.impl.data.key.UserId;

@Getter
@Setter
@Entity
@Table(name = "users", indexes = {@Index(name = "ix_email", columnList = "email"), @Index(name = "ix_users_id", columnList = "userId")})
public class User extends BaseEntity implements UserDetails {
  @Serial
  private static final long serialVersionUID = 3842546232021972948L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Setter(AccessLevel.PRIVATE)
  @Convert(converter = UserIdConverter.class)
  @Column(nullable = false, unique = true, columnDefinition = "varchar(255)")
  private UserId userId;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private Set<Address> addresses = new HashSet<>();

  @ManyToMany(mappedBy = "parents", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
  private Set<User> children = new HashSet<>();

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
  private Set<User> parents = new HashSet<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private Collection<ClubUser> clubUsers = new ArrayList<>();

  @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
  private ImageToken profileImage;

  @Column(nullable = false)
  private LocalDate dateOfBirth;

  @Column(nullable = false, length = 120, unique = true, name = "email")
  private String email;

  @Column(nullable = false)
  private String encryptedPassword;

  @Column(nullable = false, length = 50)
  private String firstName;

  private LocalDateTime lastLoginTime;

  @Column(nullable = false, length = 50)
  private String lastName;

  private boolean managedAccount;

  public User() {
    userId = new UserId(UUID.randomUUID().toString());
  }

  public User(UserId userId) {
    this.userId = userId;
  }

  public void addClubUser(ClubUser clubUser) {
    clubUsers.add(clubUser);
    clubUser.setUser(this);
  }

  public void addAddress(Address address) {
    addresses.add(address);
    address.setUser(this);
  }

  public void addChild(User child) {
    children.add(child);
    child.parents.add(this);
  }

  public void addParent(User parent) {
    parents.add(parent);
    parent.addChild(this);
  }

  public Optional<ClubUser> getClubUser(String clubId) {
    return clubUsers.stream().filter(clubUser -> clubUser.getClub().getClubId().equals(clubId)).findFirst();
  }

  public Set<Address> getAddresses() {
    if (!parents.isEmpty())
      return parents.stream()
          .map(User::getAddresses)
          .flatMap(Set::stream)
          .collect(Collectors.toSet());
    return addresses;
  }

  // TODO logic move to utility class
  public int getAge() {
    LocalDate now = LocalDate.now();
    int age = now.getYear() - dateOfBirth.getYear();
    int mn = now.getMonth().getValue();
    int mb = dateOfBirth.getMonth().getValue();
    int dn = now.getDayOfMonth();
    int db = dateOfBirth.getDayOfMonth();

    if (mn > mb)
      return age;
    if (mn < mb)
      return age - 1;
    if (dn >= db)
      return age;
    return age - 1;
  }

  @Override
  public Collection<GrantedAuthority> getAuthorities() {
    return Set.of();
  }

  @Override
  public String getPassword() {
    return encryptedPassword;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
    User other = (User) obj;
    if (userId == null) {
      return other.userId == null;
    } else
      return userId.equals(other.userId);
  }

  public void removeAddress(Address address) {
    addresses.remove(address);
    address.setUser(null);
  }

  public void removeChild(User child) {
    children.remove(child);
    child.parents.remove(this);
  }

  public void removeClubUser(ClubUser clubUser) {
    clubUsers.remove(clubUser);
    clubUser.setUser(null);
  }

  public void removeParent(User parent) {
    parents.remove(parent);
    parent.removeChild(this);
  }

  public String getProfileImageId() {
    if (profileImage == null) return null;
    return profileImage.getImageTokenId().toString();
  }
}
