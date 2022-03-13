package nu.borjessons.clubhouse.impl.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import nu.borjessons.clubhouse.impl.data.Announcement;
import nu.borjessons.clubhouse.impl.data.key.AnnouncementId;
import nu.borjessons.clubhouse.impl.dto.AnnouncementRecord;

@Repository
public interface AnnouncementRepository extends PagingAndSortingRepository<Announcement, Long> {
  Optional<Announcement> findAnnouncementByAnnouncementId(AnnouncementId announcementId);

  void deleteAnnouncementByAnnouncementId(AnnouncementId announcementId);

  List<AnnouncementRecord> findAnnouncementByClubId(long id, Pageable pageable);

  List<AnnouncementRecord> findAnnouncementsByClubIdIn(List<Long> clubIds, Pageable pageable);

  @Query(value = "SELECT COUNT(*) FROM announcement WHERE club_id IN (SELECT id FROM club WHERE club_id = ?1)", nativeQuery = true)
  int getSize(String clubId);
}
