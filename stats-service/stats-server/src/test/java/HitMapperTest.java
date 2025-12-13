
import ru.practicum.ewm.dto.EndpointHitDto;
import org.junit.jupiter.api.Test;
import stats.server.mapper.HitMapper;
import stats.server.model.EndpointHit;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HitMapperTest {

    @Test
    void toEndpointHit_ShouldMapAllFields() {
        EndpointHitDto dto = EndpointHitDto.builder()
                .id(1L)
                .app("test-app")
                .uri("/test")
                .ip("192.168.1.1")
                .timestamp(LocalDateTime.now().withNano(0))
                .build();

        EndpointHit result = HitMapper.toEndpointHit(dto);

        assertNotNull(result);
        assertEquals(dto.getId(), result.getId());
        assertEquals(dto.getApp(), result.getApp());
        assertEquals(dto.getUri(), result.getUri());
        assertEquals(dto.getIp(), result.getIp());
        assertEquals(dto.getTimestamp(), result.getTimestamp());
    }

    @Test
    void toEndpointHitDto_ShouldMapAllFields() {
        EndpointHit hit = EndpointHit.builder()
                .id(2L)
                .app("app")
                .uri("/api")
                .ip("127.0.0.1")
                .timestamp(LocalDateTime.now().withNano(0))
                .build();

        EndpointHitDto result = HitMapper.toEndpointHitDto(hit);

        assertNotNull(result);
        assertEquals(hit.getId(), result.getId());
        assertEquals(hit.getApp(), result.getApp());
        assertEquals(hit.getUri(), result.getUri());
        assertEquals(hit.getIp(), result.getIp());
        assertEquals(hit.getTimestamp(), result.getTimestamp());
    }

    @Test
    void toEndpointHit_WithNull_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> HitMapper.toEndpointHit(null));
    }

    @Test
    void toEndpointHitDto_WithNull_ShouldThrowException() {
        assertThrows(NullPointerException.class, () -> HitMapper.toEndpointHitDto(null));
    }

}