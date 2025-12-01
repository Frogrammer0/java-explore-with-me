package ru.practicum.ewm.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestsRepository extends JpaRepository<Request, Long> {

    List<Request> findByRequesterId(Long userId);

    List<Request> findByEventId(Long eventId);

    boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    long countByEventIdAndStatus(Long eventId, EventStatus status);
}
