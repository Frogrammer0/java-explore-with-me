package ru.practicum.ewm.service;

import jakarta.servlet.http.HttpServletRequest;

public interface StatsService {
    void sendHit(HttpServletRequest request);
}
