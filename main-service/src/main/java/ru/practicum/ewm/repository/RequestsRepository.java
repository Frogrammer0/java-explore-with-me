package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.model.RequestStatus;
import ru.practicum.ewm.dto.request.EventConfirmedDto;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestsRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByEventId(Long eventId);

    Boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    Long countByEventIdAndStatus(Long eventId, RequestStatus status);

    List<Request> findAllByIdIn(List<Long> ids);

    List<Request> findAllByRequesterId(Long requesterId);

    Optional<Request> findAllByRequesterIdAndEventId(Long requesterId, Long eventId);

    @Query(value = """
            select count(r)
            from Request r
            where r.event.id = :eventId AND r.status = ru.practicum.ewm.model.RequestStatus.CONFIRMED
            """)
    Long countConfirmedRequests(@Param("eventId") Long eventId);

    @Query(value = """
            select new ru.practicum.ewm.dto.request.EventConfirmedDto(e.id, COUNT(r))
            from Request r
            JOIN r.event e
            where e.id IN :eventIds AND r.status = ru.practicum.ewm.model.RequestStatus.CONFIRMED
            group by e.id
            """)
    List<EventConfirmedDto> getConfirmedRequestsByEvents(@Param("eventIds") List<Long> eventIds);
}
