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


            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl(statsUrl + "/stats")
                    .queryParam("start", start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .queryParam("end", end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .queryParam("unique", unique);

            if (uris != null && !uris.isEmpty()) {
                builder.queryParam("uris", String.join(",", uris));
            }

            String uri = builder.encode().toUriString();

            ResponseEntity<ViewStatsDto[]> response = restTemplate.getForEntity(
                    uri,
                    ViewStatsDto[].class
            );

            ViewStatsDto[] body = response.getBody();
            return body != null ? Arrays.asList(body) : List.of();


    }
}
