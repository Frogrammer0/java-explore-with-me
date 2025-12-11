package ru.practicum.ewm.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;

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
