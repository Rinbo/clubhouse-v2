package nu.borjessons.clubhouse.impl.dto;

import java.time.LocalDateTime;
import java.util.Locale;

import nu.borjessons.clubhouse.impl.data.Announcement;
import nu.borjessons.clubhouse.impl.data.ClubUser;
import nu.borjessons.clubhouse.impl.data.ImageToken;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.data.key.AnnouncementId;
import nu.borjessons.clubhouse.impl.data.key.ImageTokenId;
import nu.borjessons.clubhouse.impl.util.Validate;

public record AnnouncementRecord(
    AnnouncementId announcementId,
    String title,
    String body,
    String author,
    ImageTokenId imageTokenId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  private static String getAuthor(ClubUser clubUser) {
    if (clubUser == null) return null;
    User author = clubUser.getUser();
    return String.format(Locale.ROOT, "%s %s", author.getFirstName(), author.getLastName());
  }

  private static ImageTokenId getImageTokenId(ImageToken imageToken) {
    if (imageToken == null) return null;
    return imageToken.getImageTokenId();
  }

  public AnnouncementRecord {
    Validate.notNull(announcementId, "announcementId");
    Validate.notNull(title, "title");
    Validate.notNull(body, "body");
  }

  public AnnouncementRecord(Announcement announcement) {
    this(announcement.getAnnouncementId(),
        announcement.getTitle(),
        announcement.getBody(),
        getAuthor(announcement.getAuthor()),
        getImageTokenId(announcement.getImageToken()),
        announcement.getCreatedAt(),
        announcement.getUpdatedAt());
  }
}
