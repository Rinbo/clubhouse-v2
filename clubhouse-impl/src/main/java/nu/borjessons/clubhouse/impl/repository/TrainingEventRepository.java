package nu.borjessons.clubhouse.impl.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import nu.borjessons.clubhouse.impl.data.TrainingEvent;

public interface TrainingEventRepository extends JpaRepository<TrainingEvent, Long> {
}
