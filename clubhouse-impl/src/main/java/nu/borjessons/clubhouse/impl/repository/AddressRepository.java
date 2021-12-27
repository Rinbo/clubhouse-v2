package nu.borjessons.clubhouse.impl.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nu.borjessons.clubhouse.impl.data.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
  Optional<Address> findByAddressId(String addressId);
}
