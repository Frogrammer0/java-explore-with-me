package stats.server.service;

import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface StatsService {
    EndpointHitDto create(EndpointHitDto hitDto);

    List<ViewStatsDto> getStats(LocalDateTime startKey, LocalDateTime endKey, ArrayList<String> uris, boolean unique);
}
