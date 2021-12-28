package nu.borjessons.clubhouse.integration.tests.util;

import org.springframework.context.ConfigurableApplicationContext;

import nu.borjessons.clubhouse.impl.repository.TeamRepository;

public class DbUtil {
  public static String getTeamIdFromDatabase(ConfigurableApplicationContext configurableApplicationContext) {
    TeamRepository teamRepository = configurableApplicationContext.getBean(TeamRepository.class);
    return TeamUtil.getAllTeams(teamRepository).iterator().next().getTeamId();
  }

}
