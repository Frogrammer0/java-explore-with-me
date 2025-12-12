package ru.practicum.ewm.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.service.CompilationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
@Slf4j
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@RequestBody @Valid NewCompilationDto dto) {
        log.info("create in AdminCompilationController");
        return compilationService.create(dto);
    }

    @PatchMapping("/{compId}")
    public CompilationDto edit(@PathVariable Long compId,
                               @RequestBody @Valid CompilationDto dto) {
        log.info("edit in AdminCompilationController");
        return compilationService.edit(compId, dto);
    }

    @DeleteMapping("/{compId}")
    public void delete(@PathVariable Long compId) {
        log.info("delete in AdminCompilationController");
        compilationService.delete(compId);
    }
}
