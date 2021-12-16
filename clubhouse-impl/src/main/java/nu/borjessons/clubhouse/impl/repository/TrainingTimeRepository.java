package nu.borjessons.clubhouse.impl.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import nu.borjessons.clubhouse.impl.data.TrainingTime;

public interface TrainingTimeRepository extends JpaRepository<TrainingTime, Long> {
  Optional<TrainingTime> findByTrainingTimeId();
}
