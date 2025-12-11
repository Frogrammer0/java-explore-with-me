package ru.practicum.ewm.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.service.StatsService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsClient statsClient;

    @Value("${stats-app-name:ewm-main-service}")
    private String app;

    @Override
    public void sendHit(HttpServletRequest request) {
        EndpointHitDto dto = new EndpointHitDto(
                null,
                app,
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        );
        statsClient.sendHit(dto);
    }
}
