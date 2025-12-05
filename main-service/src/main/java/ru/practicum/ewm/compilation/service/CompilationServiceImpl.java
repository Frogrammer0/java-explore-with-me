package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.common.exception.NotFoundException;
import ru.practicum.ewm.compilation.Compilation;
import ru.practicum.ewm.compilation.CompilationMapper;
import ru.practicum.ewm.compilation.CompilationRepository;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;


    @Override
    public CompilationDto create(NewCompilationDto compilationDto) {
        log.info("создание подборки в CompilationServiceImpl с id = {}", compilationDto);
        List<Event> events = eventRepository.findAllById(compilationDto.getEvents());
        if (compilationDto.getEvents().size() < events.size()) {
            throw new NotFoundException("найдены не все события из подборки");
        }
        Compilation compilation = compilationMapper.toCompilation(compilationDto, events);

        return compilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public void delete(Long compId) {
        log.info("удаление подборки в CompilationServiceImpl с id = {}", compId);
        getCompilationOrThrow(compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto edit(Long compId, NewCompilationDto compDto) {
        log.info("изменение подборки в CompilationServiceImpl с id = {}", compId);
        Compilation comp = getCompilationOrThrow(compId);

        if (compDto.getTitle() != null) comp.setTitle(compDto.getTitle());
        if (compDto.getPinned() != null) comp.setPinned(compDto.getPinned());
        if (compDto.getEvents() != null && !compDto.getEvents().isEmpty()) {
            comp.setEvents(eventRepository.findAllByIdIn(compDto.getEvents()));
        }

        return compilationMapper.toCompilationDto(compilationRepository.save(comp));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        log.info("получение подборки в CompilationServiceImpl с pinned = {}", pinned);

        Pageable page = PageRequest.of(from / size, size);
        if (pinned != null) {
            return compilationRepository.findByPinned(pinned, page).stream()
                    .map(compilationMapper::toCompilationDto)
                    .collect(Collectors.toList());
        } else {
            return compilationRepository.findAll(page).stream()
                    .map(compilationMapper::toCompilationDto)
                    .collect(Collectors.toList());
        }

    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilation(Long compId) {
        log.info("получение подборки в CompilationServiceImpl с id = {}", compId);
        return compilationMapper.toCompilationDto(getCompilationOrThrow(compId));
    }


    private Compilation getCompilationOrThrow(Long compId) {
        return compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("не найдена подборка с id =" + compId)
        );
    }
}
