package nu.borjessons.clubhouse.impl.repository;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.data.TrainingEvent;

public interface TrainingEventRepository extends JpaRepository<TrainingEvent, Long> {
  @Query("select t from TrainingEvent t where t.team = ?1")
  List<TrainingEvent> findByTeam(Team team, PageRequest pageRequest);

  @Query("select t from TrainingEvent t where t.team in ?1")
  List<TrainingEvent> findByTeamIn(List<Team> teams, PageRequest of);
}
