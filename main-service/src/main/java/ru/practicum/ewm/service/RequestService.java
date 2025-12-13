package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> getUserRequests(Long userId);

    ParticipationRequestDto create(Long userId, Long eventId);

    ParticipationRequestDto cancel(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequestByEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateStatuses(Long userId, Long eventId,
                                                   EventRequestStatusUpdateRequest updateRequests);

}
