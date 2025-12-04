package ru.practicum.ewm.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByInitiatorId(Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long id, Long initiatorId);

    List<Event> findAllByIdIn(List<Long> ids);

    Boolean existsByIdAndState(Long eventId, EventState state);

    Boolean existsByCategoryId(Long id);

    Boolean existsByIdAndInitiatorId(Long eventId, Long initiatorId);

}
