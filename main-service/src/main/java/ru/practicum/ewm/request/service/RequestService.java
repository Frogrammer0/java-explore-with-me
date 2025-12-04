package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> getUserRequests(Long userId);

    ParticipationRequestDto create(Long userId, Long EventId);

    ParticipationRequestDto cancel(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequestByEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateStatuses(Long userId, Long eventId,
                                                   EventRequestStatusUpdateRequest updateRequests);

}
