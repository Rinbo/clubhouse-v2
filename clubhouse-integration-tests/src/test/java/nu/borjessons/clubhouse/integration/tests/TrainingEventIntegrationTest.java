package nu.borjessons.clubhouse.integration.tests;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;

import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;

class TrainingEventIntegrationTest {

  @Test
  void createTest() throws IOException {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {

    }
  }
}
