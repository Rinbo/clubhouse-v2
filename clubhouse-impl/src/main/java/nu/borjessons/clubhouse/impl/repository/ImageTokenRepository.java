package nu.borjessons.clubhouse.impl.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nu.borjessons.clubhouse.impl.data.ImageToken;
import nu.borjessons.clubhouse.impl.data.key.ImageTokenId;

@Repository
public interface ImageTokenRepository extends JpaRepository<ImageToken, Long> {
  Optional<ImageToken> findByImageTokenId(ImageTokenId imageTokenId);
}
