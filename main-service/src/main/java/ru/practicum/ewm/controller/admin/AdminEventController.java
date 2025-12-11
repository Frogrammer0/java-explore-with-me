package ru.practicum.ewm.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.ewm.model.EventState;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.UpdateEventAdminRequest;
import ru.practicum.ewm.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
@Slf4j
@Validated
public class AdminEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getEvents(@RequestParam List<Long> users,
                                        @RequestParam List<String> states,
                                        @RequestParam List<Long> categories,
                                        @RequestParam(required = false) String startRange,
                                        @RequestParam(required = false) String endRange,
                                        @RequestParam(defaultValue = "0") int from,
                                        @RequestParam(defaultValue = "10") int size) {
        log.info("getEvents in AdminEventController");
        LocalDateTime start = (startRange != null) ? LocalDateTime.parse(startRange) : null;
        LocalDateTime end = (endRange != null) ? LocalDateTime.parse(endRange) : null;
        List<EventState> stateList = states.stream().map(EventState::valueOf).toList();

        return eventService.getEventsForAdmin(users, stateList, categories, start, end, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto edit(@PathVariable Long eventId,
                             @RequestBody @Valid UpdateEventAdminRequest dto) {
        log.info("edit in AdminEventController");
        return eventService.updateEventByAdmin(eventId, dto);
    }


}
