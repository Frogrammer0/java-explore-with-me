package ru.practicum.ewm.compilation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventMapper;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public class CompilationMapper {
    EventMapper eventMapper;


    public Compilation toCompilation(NewCompilationDto newCompDto, List<Event> events) {
        return Compilation.builder()
                .events(events)
                .title(newCompDto.getTitle())
                .pinned(newCompDto.getPinned())
                .build();
    }

    public CompilationDto toCompilationDto(Compilation comp) {
        return CompilationDto.builder()
                .id(comp.getId())
                .pinned(comp.getPinned())
                .title(comp.getTitle())
                .events(
                        comp.getEvents().stream()
                                .map(eventMapper::toEventShortDto)
                                .collect(Collectors.toList())
                )
                .build();
    }
}
