package nu.borjessons.clubhouse.impl.repository;

import nu.borjessons.clubhouse.impl.data.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
  Optional<Address> getByAddressId(String addressId);
}
