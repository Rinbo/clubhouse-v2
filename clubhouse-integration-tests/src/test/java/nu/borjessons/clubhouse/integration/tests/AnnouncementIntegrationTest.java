package nu.borjessons.clubhouse.integration.tests;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;

import nu.borjessons.clubhouse.impl.dto.AnnouncementRecord;
import nu.borjessons.clubhouse.impl.dto.UserDto;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.AnnouncementUtil;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import nu.borjessons.clubhouse.integration.tests.util.UserUtil;

class AnnouncementIntegrationTest {
  private static final String DEFAULT_ANNOUNCEMENT_TITLE = "My announcement";

  @Test
  void createAnnouncementTest() throws IOException {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      AnnouncementRecord announcementRecord = AnnouncementUtil.createAnnouncement(token, EmbeddedDataLoader.CLUB_ID, DEFAULT_ANNOUNCEMENT_TITLE);

      verifyAnnouncementRecord(announcementRecord);
    }
  }

  @Test
  void getSingleAnnouncement() throws IOException {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      AnnouncementRecord announcementRecord = AnnouncementUtil.getAnnouncement(token, EmbeddedDataLoader.CLUB_ID,
          AnnouncementUtil.createAnnouncement(token, EmbeddedDataLoader.CLUB_ID, DEFAULT_ANNOUNCEMENT_TITLE).announcementId());

      verifyAnnouncementRecord(announcementRecord);
    }
  }

  @Test
  void updateAnnouncement() throws IOException {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      AnnouncementRecord announcementRecord = AnnouncementUtil.updateAnnouncement(token, EmbeddedDataLoader.CLUB_ID,
          AnnouncementUtil.createAnnouncement(token, EmbeddedDataLoader.CLUB_ID, DEFAULT_ANNOUNCEMENT_TITLE).announcementId());

      verifyAnnouncementRecord(announcementRecord, "updated body", "updated title");
    }
  }

  @Test
  void deleteAnnouncement() throws IOException {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      AnnouncementRecord announcementRecord = AnnouncementUtil.createAnnouncement(token, EmbeddedDataLoader.CLUB_ID, DEFAULT_ANNOUNCEMENT_TITLE);
      AnnouncementUtil.deleteAnnouncement(token, EmbeddedDataLoader.CLUB_ID, announcementRecord.announcementId());

      try {
        AnnouncementUtil.getAnnouncement(token, EmbeddedDataLoader.CLUB_ID, announcementRecord.announcementId());
      } catch (HttpClientErrorException e) {
        Assertions.assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
      }
    }
  }

  @Test
  void getAllRecordsAndVerifyForeignKeyConstraintRemovedOnUserDeletionTest() throws IOException {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      verifyAnnouncementRecord(AnnouncementUtil.createAnnouncement(token, EmbeddedDataLoader.CLUB_ID, DEFAULT_ANNOUNCEMENT_TITLE));
      verifyAnnouncementRecord(AnnouncementUtil.createAnnouncement(token, EmbeddedDataLoader.CLUB_ID, DEFAULT_ANNOUNCEMENT_TITLE));
      Assertions.assertEquals(2, AnnouncementUtil.getAllAnnouncements(token, EmbeddedDataLoader.CLUB_ID).size());

      UserUtil.deleteSelf(token);

      List<AnnouncementRecord> announcementRecords = AnnouncementUtil.getAllAnnouncements(
          UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD), EmbeddedDataLoader.CLUB_ID);

      Assertions.assertEquals(2, announcementRecords.size());
      announcementRecords.forEach(announcementRecord -> Assertions.assertNull(announcementRecord.author()));
    }
  }

  @Test
  void verifyForeignKeyConstraintRemovedOnClubUserRemovalTest() throws IOException {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      UserDto adminUser = UserUtil.getSelf(token);

      verifyAnnouncementRecord(AnnouncementUtil.createAnnouncement(token, EmbeddedDataLoader.CLUB_ID, DEFAULT_ANNOUNCEMENT_TITLE));
      verifyAnnouncementRecord(AnnouncementUtil.createAnnouncement(token, EmbeddedDataLoader.CLUB_ID, DEFAULT_ANNOUNCEMENT_TITLE));
      Assertions.assertEquals(2, AnnouncementUtil.getAllAnnouncements(token, EmbeddedDataLoader.CLUB_ID).size());

      UserUtil.removeClubUser(EmbeddedDataLoader.CLUB_ID, token, adminUser.getUserId());

      List<AnnouncementRecord> announcementRecords = AnnouncementUtil.getAllAnnouncements(
          UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD), EmbeddedDataLoader.CLUB_ID);

      Assertions.assertEquals(2, announcementRecords.size());
      announcementRecords.forEach(announcementRecord -> Assertions.assertNull(announcementRecord.author()));
    }
  }

  private void verifyAnnouncementRecord(AnnouncementRecord announcementRecord, String body, String title) {
    Assertions.assertNotNull(announcementRecord);
    Assertions.assertEquals(title, announcementRecord.title());
    Assertions.assertEquals(body, announcementRecord.body());
    Assertions.assertNotNull(announcementRecord.announcementId());
  }

  private void verifyAnnouncementRecord(AnnouncementRecord announcementRecord) {
    verifyAnnouncementRecord(announcementRecord, "body", DEFAULT_ANNOUNCEMENT_TITLE);
  }
}
