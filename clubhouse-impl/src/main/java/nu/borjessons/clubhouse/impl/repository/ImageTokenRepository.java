package nu.borjessons.clubhouse.impl.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import nu.borjessons.clubhouse.impl.data.ImageToken;
import nu.borjessons.clubhouse.impl.data.key.ImageTokenId;

@Repository
public interface ImageTokenRepository extends JpaRepository<ImageToken, Long> {
  @Modifying
  @Query("delete from ImageToken i where i.imageTokenId = ?1")
  void deleteAllByImageTokenId(List<ImageTokenId> imageTokens);

  Optional<ImageToken> findByImageTokenId(ImageTokenId imageTokenId);
}
