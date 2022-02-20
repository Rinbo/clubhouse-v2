package nu.borjessons.clubhouse.impl.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import nu.borjessons.clubhouse.impl.data.Announcement;
import nu.borjessons.clubhouse.impl.data.key.AnnouncementId;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
  Optional<Announcement> findAnnouncementByAnnouncementId(AnnouncementId announcementId);

  void deleteAnnouncementByAnnouncementId(AnnouncementId announcementId);
}
