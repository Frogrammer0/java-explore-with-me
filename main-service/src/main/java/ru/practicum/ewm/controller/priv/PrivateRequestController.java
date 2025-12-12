package ru.practicum.ewm.controller.priv;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateRequestController {
    private final RequestService requestService;


    @GetMapping("/requests")
    public List<ParticipationRequestDto> getRequests(@PathVariable Long userId) {
        log.info("getRequests in PrivateRequestController");
        return requestService.getUserRequests(userId);
    }

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto create(@PathVariable Long userId,
                                          @RequestParam(name = "eventId") @NotNull Long eventId) {
        log.info("create in PrivateRequestController");
        return requestService.create(userId, eventId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public void cancelRequest(@PathVariable Long userId,
                              @PathVariable Long requestId) {
        log.info("cancelRequest in PrivateRequestController");
        requestService.cancel(userId, requestId);
    }

    @GetMapping("/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsByEvent(@PathVariable Long userId,
                                                            @PathVariable Long eventId) {
        log.info("getRequestsByEvent in PrivateRequestController");
        return requestService.getRequestByEvent(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    public EventRequestStatusUpdateResult editRequestsByEvent(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        return requestService.updateStatuses(userId, eventId, updateRequest);
    }
}
