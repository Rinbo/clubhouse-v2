package nu.borjessons.clubhouse.impl.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import nu.borjessons.clubhouse.impl.data.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
  Optional<Schedule> findByTeamId(String teamId);
}
