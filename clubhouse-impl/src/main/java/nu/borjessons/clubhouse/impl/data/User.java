package nu.borjessons.clubhouse.impl.data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user", indexes = {@Index(name = "ix_email", columnList = "email"), @Index(name = "ix_user_id", columnList = "userId")})
public class User extends BaseEntity implements UserDetails {
  private static final long serialVersionUID = -1098642930133262484L;

  @Id
  @GeneratedValue
  private long id;

  @Column(nullable = false, unique = true)
  private final String userId;

  @OneToMany(
      mappedBy = "user",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  private Set<Address> addresses = new HashSet<>();

  @ManyToMany(mappedBy = "parents", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
  private Set<User> children = new HashSet<>();

  @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
  private Set<User> parents = new HashSet<>();

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
    userId = UUID.randomUUID().toString();
  }

  public User(String userId) {
    this.userId = userId;
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

  public Set<Address> getAddresses() {
    if (!parents.isEmpty())
      return parents.stream()
          .map(User::getAddresses)
          .flatMap(Set::stream)
          .collect(Collectors.toSet());
    return addresses;
  }

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

  public void removeParent(User parent) {
    parents.remove(parent);
    parent.removeChild(this);
  }
}
