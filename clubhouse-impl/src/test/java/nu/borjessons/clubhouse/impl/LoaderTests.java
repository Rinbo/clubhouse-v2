package nu.borjessons.clubhouse.impl;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.repository.ClubRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

@RequiredArgsConstructor
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "test")
class LoaderTests {

  private static final String ALLES_IK = "Alles IK";

  private final ClubRepository clubRepository;

  @Test
  void testLoadDataForTestClass() {
    Assertions.assertEquals(ALLES_IK, clubRepository.findByName(ALLES_IK).getName());
  }
}
