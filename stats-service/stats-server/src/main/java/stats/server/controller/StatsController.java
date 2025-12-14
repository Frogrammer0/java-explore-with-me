package stats.server.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import stats.server.service.StatsService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping
public class StatsController {

    private final StatsService statsService;


    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto postHits(
            @RequestBody @Valid EndpointHitDto hitDto
    ) {
        log.info("@PostMapping(\"/hit\") with hitDto = {}", hitDto);
        return statsService.create(hitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(
            @RequestParam(name = "start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam(name = "end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(name = "uris", required = false) ArrayList<String> uris,
            @RequestParam(name = "unique", required = false, defaultValue = "false") boolean unique
    ) {
        log.info("@GetMapping(\"/stats\") with start = {}, end = {}, uris = {}, unique = {}", start, end, uris, unique);
        if (start.isAfter(end)) throw new IllegalArgumentException("время начала позже времени конца");

        return statsService.getStats(start, end, uris, unique);
    }

}
