package nu.borjessons.clubhouse.integration.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;

import nu.borjessons.clubhouse.impl.data.key.ImageTokenId;
import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.dto.BaseUserRecord;
import nu.borjessons.clubhouse.impl.dto.ClubUserDto;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.ImageUtil;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import nu.borjessons.clubhouse.integration.tests.util.UserUtil;

class ImageIntegrationTest {
  @Test
  void uploadClubLogo() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      ImageTokenId imageTokenId = ImageUtil.uploadClubLogo(token, EmbeddedDataLoader.CLUB_ID);
      Assertions.assertNotNull(imageTokenId);
    }
  }

  @Test
  void userUploadsProfileImage() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.USER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      ImageTokenId imageTokenId = ImageUtil.uploadProfileImage(token, EmbeddedDataLoader.USER_ID);
      Assertions.assertNotNull(imageTokenId);
    }
  }

  @Test
  void dadUploadsImageForChild() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      ClubUserDto clubUserDto = UserUtil.getClubUserPrincipal(EmbeddedDataLoader.CLUB_ID, token);
      BaseUserRecord childRecord = clubUserDto.getChildren().stream().findFirst().orElseThrow();
      ImageTokenId imageTokenId = ImageUtil.uploadProfileImage(token, new UserId(childRecord.userId()));
      Assertions.assertNotNull(imageTokenId);
    }
  }

  @Test
  void dadUploadsImageForRandomChild() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      try {
        ImageUtil.uploadProfileImage(token, new UserId("random"));
      } catch (HttpClientErrorException e) {
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
      }
    }
  }
}
