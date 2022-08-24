package nu.borjessons.clubhouse.impl.repository;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.data.TrainingEvent;
import nu.borjessons.clubhouse.impl.dto.TrainingEventRecord;

public interface TrainingEventRepository extends JpaRepository<TrainingEvent, Long> {
  List<TrainingEventRecord> findByTeam(Team team, PageRequest pageRequest);
}
