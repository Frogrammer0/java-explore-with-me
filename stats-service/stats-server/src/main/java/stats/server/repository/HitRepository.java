package stats.server.repository;

import ru.practicum.ewm.dto.ViewStatsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import stats.server.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public interface HitRepository extends JpaRepository<EndpointHit, Long> {


    @Query("""
            SELECT new ru.practicum.ewm.dto.ViewStatsDto(
            h.app,
            h.uri,
            COUNT(h))
            FROM EndpointHit h
            WHERE h.timestamp BETWEEN :start AND :end
            GROUP BY h.app, h.uri
            ORDER BY COUNT(h) DESC
            """)
    List<ViewStatsDto> getStats(@Param("start") LocalDateTime start,
                                @Param("end") LocalDateTime end);

    @Query("""
            SELECT new ru.practicum.ewm.dto.ViewStatsDto(
            h.app,
            h.uri,
            COUNT(DISTINCT h.ip))
            FROM EndpointHit h
            WHERE h.timestamp BETWEEN :start AND :end
            GROUP BY h.app, h.uri
            ORDER BY COUNT(DISTINCT h.ip) DESC
            """)
    List<ViewStatsDto> getStatsUnique(@Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end);

    @Query("""
            SELECT new ru.practicum.ewm.dto.ViewStatsDto(
            h.app,
            h.uri,
            COUNT(h))
            FROM EndpointHit h
            WHERE h.timestamp BETWEEN :start AND :end
                AND h.uri IN :uris
            GROUP BY h.app, h.uri
            ORDER BY COUNT(h) DESC
            """)
    List<ViewStatsDto> getStatsWithUris(@Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end,
                                        @Param("uris") List<String> uris);


    @Query("""
            SELECT new ru.practicum.ewm.dto.ViewStatsDto(
            h.app,
            h.uri,
            COUNT(DISTINCT h.ip))
            FROM EndpointHit h
            WHERE h.timestamp BETWEEN :start AND :end
                AND h.uri IN :uris
            GROUP BY h.app, h.uri
            ORDER BY COUNT(DISTINCT h.ip) DESC
            """)
    List<ViewStatsDto> getStatsUniqueWithUris(@Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end,
                                              @Param("uris") ArrayList<String> uris);
}
