package nu.borjessons.clubhouse.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nu.borjessons.clubhouse.data.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
	
	Optional<Address> getByAddressId(String addressId);

}
