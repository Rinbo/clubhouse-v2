package nu.borjessons.clubhouse.impl;

import nu.borjessons.clubhouse.impl.repository.ClubRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "test")
class LoaderTests {

  private static final String ALLES_IK = "Alles IK";

  @Autowired private ClubRepository clubRepository;

  @Test
  void testLoadDataForTestClass() {
    Assertions.assertEquals(ALLES_IK, clubRepository.findByName(ALLES_IK).getName());
  }
}
