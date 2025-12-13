package stats.server.service;

import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stats.server.mapper.HitMapper;
import stats.server.model.EndpointHit;
import stats.server.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@NoArgsConstructor(force = true)
public class StatsServiceImpl implements StatsService {
    HitRepository hitRepository;

    @Autowired
    public StatsServiceImpl(HitRepository hitRepository) {
        this.hitRepository = hitRepository;
    }

    @Override
    public EndpointHitDto create(EndpointHitDto hitDto) {
        log.info("create Hit in StatsServiceImpl");
        EndpointHit hit = HitMapper.toEndpointHit(hitDto);
        return HitMapper.toEndpointHitDto(hitRepository.save(hit));
    }

    @Override
    public List<ViewStatsDto> getStats(
            LocalDateTime start,
            LocalDateTime end,
            ArrayList<String> uris,
            boolean unique
    ) {
        log.info("get Stats in StatsServiceImpl between start = {}, end = {}, uris = {}, unique = {}",
                start, end, uris, unique);

        if (uris == null || uris.isEmpty()) {
            if (unique) {
                return hitRepository.getStatsUnique(start, end);
            } else {
                return hitRepository.getStats(start, end);
            }
        }
        if (unique) {
            return hitRepository.getStatsUniqueWithUris(start, end, uris);
        } else {
            return hitRepository.getStatsWithUris(start, end, uris);
        }
    }


}
