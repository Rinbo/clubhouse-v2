package nu.borjessons.clubhouse;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import nu.borjessons.clubhouse.repository.ClubRepository;

@SpringBootTest(webEnvironment =WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "test")
class LoaderTests {
	
	private static final String ALLES_IK = "Alles IK";
	
    @Autowired
    private ClubRepository clubRepository;
 
    @Test
    void testLoadDataForTestClass() {
        assertEquals(ALLES_IK, clubRepository.findByName(ALLES_IK).getName());
    }

}
