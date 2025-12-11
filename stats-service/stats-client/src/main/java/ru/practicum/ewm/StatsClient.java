package ru.practicum.ewm;

import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.exception.IllegalArgumentException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Component
public class StatsClient {
    private final RestTemplate restTemplate;
    private final String statsUrl;

    public StatsClient(@Value("${stats-server.url}") String statsUrl) {
        this.restTemplate = new RestTemplate();
        this.statsUrl = statsUrl;
    }


    public void sendHit(EndpointHitDto hitDto) {
        restTemplate.postForEntity(
                statsUrl + "/hit",
                hitDto,
                Void.class
        );
    }

    public List<ViewStatsDto> getStats(
            LocalDateTime start,
            LocalDateTime end,
            List<String> uris,
            boolean unique
    ) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("даты начала и конца диапазона должны быть заданы");
        }

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Дата начала не может быть позже даты окончания");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(statsUrl + "/stats")
                .queryParam("start", start.format(formatter))
                .queryParam("end", end.format(formatter))
                .queryParam("unique", unique);

        if (uris != null && !uris.isEmpty()) {
            uris.forEach(uri -> builder.queryParam("uris", uri));
        }


        ResponseEntity<ViewStatsDto[]> response = restTemplate.getForEntity(
                builder.toUriString(),
                ViewStatsDto[].class
        );

        ViewStatsDto[] body = response.getBody();
        return body != null ? Arrays.asList(body) : List.of();
    }
}
