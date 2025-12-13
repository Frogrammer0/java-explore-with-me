import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import stats.server.model.EndpointHit;
import stats.server.repository.HitRepository;
import stats.server.service.StatsServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsServiceImplTest {

    @Mock
    private HitRepository hitRepository;

    @InjectMocks
    private StatsServiceImpl statsService;

    @Test
    void create_ShouldSaveAndReturnDto() {
        EndpointHitDto inputDto = EndpointHitDto.builder()
                .app("test-app")
                .uri("/test")
                .ip("192.168.1.1")
                .timestamp(LocalDateTime.now().withNano(0))
                .build();

        EndpointHit savedHit = EndpointHit.builder()
                .id(1L)
                .app("test-app")
                .uri("/test")
                .ip("192.168.1.1")
                .timestamp(inputDto.getTimestamp())
                .build();

        when(hitRepository.save(any(EndpointHit.class))).thenReturn(savedHit);

        EndpointHitDto result = statsService.create(inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(hitRepository).save(any(EndpointHit.class));
    }

    @Test
    void getStats_WithoutUris_NotUnique_ShouldCallGetStats() {
        LocalDateTime start = LocalDateTime.now().withNano(0).minusHours(1);
        LocalDateTime end = LocalDateTime.now().withNano(0);
        List<ViewStatsDto> expectedStats = List.of(
                new ViewStatsDto("app1", "/uri1", 10L),
                new ViewStatsDto("app2", "/uri2", 5L)
        );

        when(hitRepository.getStats(start, end)).thenReturn(expectedStats);

        List<ViewStatsDto> result = statsService.getStats(start, end, null, false);

        assertEquals(expectedStats, result);
        verify(hitRepository).getStats(start, end);
    }

    @Test
    void getStats_WithoutUris_Unique_ShouldCallGetStatsUnique() {
        LocalDateTime start = LocalDateTime.now().withNano(0).minusHours(1);
        LocalDateTime end = LocalDateTime.now().withNano(0);
        List<ViewStatsDto> expectedStats = List.of(new ViewStatsDto("app1", "/uri1", 3L));

        when(hitRepository.getStatsUnique(start, end)).thenReturn(expectedStats);

        List<ViewStatsDto> result = statsService.getStats(start, end, null, true);

        assertEquals(expectedStats, result);
        verify(hitRepository).getStatsUnique(start, end);
    }

    @Test
    void getStats_WithUris_NotUnique_ShouldCallGetStatsWithUris() {
        LocalDateTime start = LocalDateTime.now().withNano(0).minusHours(1);
        LocalDateTime end = LocalDateTime.now().withNano(0);
        ArrayList<String> uris = new ArrayList<>(List.of("/uri1", "/uri2"));
        List<ViewStatsDto> expectedStats = List.of(new ViewStatsDto("app1", "/uri1", 7L));

        when(hitRepository.getStatsWithUris(start, end, uris)).thenReturn(expectedStats);

        List<ViewStatsDto> result = statsService.getStats(start, end, uris, false);

        assertEquals(expectedStats, result);
        verify(hitRepository).getStatsWithUris(start, end, uris);
    }

    @Test
    void getStats_WithUris_Unique_ShouldCallGetStatsUniqueWithUris() {
        LocalDateTime start = LocalDateTime.now().withNano(0).minusHours(1);
        LocalDateTime end = LocalDateTime.now().withNano(0);
        ArrayList<String> uris = new ArrayList<>(List.of("/uri1", "/uri2"));
        List<ViewStatsDto> expectedStats = List.of(new ViewStatsDto("app1", "/uri1", 2L));

        when(hitRepository.getStatsUniqueWithUris(start, end, uris)).thenReturn(expectedStats);

        List<ViewStatsDto> result = statsService.getStats(start, end, uris, true);

        assertEquals(expectedStats, result);
        verify(hitRepository).getStatsUniqueWithUris(start, end, uris);
    }
}