package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.enums.EventState;

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

    @Query("""
            select e
            from Event e
            where (:users is NULL or e.initiator.id in :users)
            and (:states is NULL or e.state in :states)
            and (:categories is NULL or e.category.id in :categories)
            and (e.eventDate >= :rangeStart)
            and (e.eventDate <= coalesce(:rangeEnd, e.eventDate))
            """)
    List<Event> findAdminEvents(@Param("users") List<Long> users,
                                @Param("states") List<EventState> states,
                                @Param("categories") List<Long> categories,
                                @Param("rangeStart") LocalDateTime rangeStart,
                                @Param("rangeEnd") LocalDateTime rangeEnd,
                                Pageable page);

    @Query("""
            select e
            from Event e
            where e.state = :state
            and (lower(e.annotation) LIKE lower(concat('%', :text, '%'))
                or lower(e.description) LIKE lower(concat('%', :text, '%')))
            and (:categories is NULL or e.category.id IN :categories)
            and (:paid is NULL or e.paid = :paid)
            and (e.eventDate >= :rangeStart)
            and (e.eventDate <= coalesce(:rangeEnd, e.eventDate))
            order by e.eventDate asc
            """)
    List<Event> findPublicEvents(@Param("state") EventState state,
                                 @Param("text") String text,
                                 @Param("categories") List<Long> categories,
                                 @Param("paid") Boolean paid,
                                 @Param("rangeStart") LocalDateTime rangeStart,
                                 @Param("rangeEnd") LocalDateTime rangeEnd,
                                 Pageable page);
}
