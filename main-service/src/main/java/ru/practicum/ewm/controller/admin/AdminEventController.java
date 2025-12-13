package ru.practicum.ewm.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.UpdateEventAdminRequest;
import ru.practicum.ewm.model.EventState;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
@Slf4j
@Validated
public class AdminEventController {

    private final EventService eventService;
    private final StatsService statsService;

    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) List<String> states,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false)
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                        LocalDateTime rangeStart,
                                        @RequestParam(required = false)
                                        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                        LocalDateTime rangeEnd,
                                        @RequestParam(defaultValue = "0") int from,
                                        @RequestParam(defaultValue = "10") int size,
                                        HttpServletRequest request) {
        log.info("getEvents in AdminEventController");
        //statsService.sendHit(request);

        List<EventState> stateList = List.of();
        if (states != null && !states.isEmpty()) {
            stateList = states.stream().map(EventState::valueOf).toList();
        }
        return eventService.getEventsForAdmin(users, stateList, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto edit(@PathVariable Long eventId,
                             @RequestBody @Valid UpdateEventAdminRequest dto) {
        log.info("edit in AdminEventController");
        return eventService.updateEventByAdmin(eventId, dto);
    }


}
