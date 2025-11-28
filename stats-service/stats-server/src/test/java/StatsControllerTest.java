
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import stats.server.controller.StatsController;
import stats.server.service.StatsService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class StatsControllerTest {

    @Mock
    private StatsService statsService;

    @InjectMocks
    private StatsController statsController;

    @Test
    void postHits_ShouldCallServiceAndReturnResult() {
        EndpointHitDto inputDto = new EndpointHitDto();
        inputDto.setApp("test-app");
        inputDto.setUri("/test");

        EndpointHitDto expectedDto = new EndpointHitDto();
        expectedDto.setApp("test-app");
        expectedDto.setUri("/test");

        when(statsService.create(any(EndpointHitDto.class))).thenReturn(expectedDto);

        EndpointHitDto result = statsController.postHits(inputDto);

        assertNotNull(result);
        assertEquals("test-app", result.getApp());
        verify(statsService).create(inputDto);
    }

    @Test
    void getStats_ShouldParseParametersAndCallService() {
        LocalDateTime start = LocalDateTime.parse("2023-01-01T10:00:00");
        LocalDateTime end = LocalDateTime.parse("2023-01-01T12:00:00");
        ArrayList<String> uris = new ArrayList<>(List.of("/test", "/api"));
        boolean unique = true;

        ViewStatsDto viewStats = new ViewStatsDto();
        viewStats.setApp("test-app");
        viewStats.setUri("/test");
        viewStats.setHits(15L);

        when(statsService.getStats(any(LocalDateTime.class), any(LocalDateTime.class), any(), anyBoolean()))
                .thenReturn(List.of(viewStats));

        List<ViewStatsDto> result = statsController.getStats(start, end, uris, unique);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("/test", result.get(0).getUri());

        verify(statsService).getStats(
                start,
                end,
                uris,
                unique
        );
    }

    @Test
    void getStats_WithoutUris_ShouldCallServiceWithNullUris() {
        LocalDateTime start = LocalDateTime.parse("2023-01-01T10:00:00");
        LocalDateTime end = LocalDateTime.parse("2023-01-01T12:00:00");

        when(statsService.getStats(any(LocalDateTime.class), any(LocalDateTime.class), any(), anyBoolean()))
                .thenReturn(List.of());

        List<ViewStatsDto> result = statsController.getStats(start, end, null, false);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}