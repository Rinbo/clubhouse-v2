package nu.borjessons.clubhouse.impl.repository;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.data.TrainingEvent;

public interface TrainingEventRepository extends JpaRepository<TrainingEvent, Long> {
  List<TrainingEvent> findByTeam(Team team, PageRequest pageRequest);
}
