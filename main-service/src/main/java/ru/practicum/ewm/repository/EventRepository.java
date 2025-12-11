package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long id, Long initiatorId);

    List<Event> findAllByIdIn(List<Long> ids);

    Boolean existsByIdAndState(Long eventId, EventState state);

    Boolean existsByCategoryId(Long id);

    Boolean existsByIdAndInitiatorId(Long eventId, Long initiatorId);

    @Query(value = """
            select e
            from Event e
            where (:users is NULL or e.initiator.id in :users)
            and (:states is NULL or e.state in :states)
            and (:categories is NULL or e.category.id in :categories)
            and (:startRange is NULL or e.eventDate >= :startRange)
            and (:endRange is NULL or e.eventDate <= :endRange)
            """)
    List<Event> findAdminEvents(@Param("users") List<Long> users,
                                @Param("states") List<EventState> states,
                                @Param("categories") List<Long> categories,
                                @Param("start") LocalDateTime startRange,
                                @Param("end") LocalDateTime endRange,
                                Pageable page);

    @Query(value = """
            select e
            from Event e
            where e.state = ru.practicum.ewm.model.EventState.PUBLISHED
            and (lower(e.annotation) LIKE lower(concat('%', :text, '%'))
            or lower(e.description) LIKE lower(concat('%', :text, '%')))
            and (:categories is NULL or e.category.id IN :categories)
            and (e.paid = :paid)
            and (:startRange is NULL or e.eventDate >= :startRange)
            and (:endRange is NULL or e.eventDate <= :endRange)
            order by e.eventDate asc
            """)
    List<Event> findPublicEvents(@Param("text") String text,
                                 @Param("categories") List<Long> categories,
                                 @Param("paid") Boolean paid,
                                 @Param("startRange") LocalDateTime startRange,
                                 @Param("endRange") LocalDateTime endRange,
                                 Pageable page);
}
