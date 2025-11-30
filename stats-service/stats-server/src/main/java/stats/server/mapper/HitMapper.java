package stats.server.mapper;

import ru.practicum.ewm.dto.EndpointHitDto;
import lombok.RequiredArgsConstructor;
import stats.server.model.EndpointHit;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HitMapper {

    public static EndpointHit toEndpointHit(EndpointHitDto hitDto) {
        return EndpointHit.builder()
                .id(hitDto.getId())
                .app(hitDto.getApp())
                .uri(hitDto.getUri())
                .ip(hitDto.getIp())
                .timestamp(hitDto.getTimestamp())
                .build();
    }

    public static EndpointHitDto toEndpointHitDto(EndpointHit hit) {
        return EndpointHitDto.builder()
                .id(hit.getId())
                .app(hit.getApp())
                .uri(hit.getUri())
                .ip(hit.getIp())
                .timestamp(hit.getTimestamp())
                .build();
    }

}
