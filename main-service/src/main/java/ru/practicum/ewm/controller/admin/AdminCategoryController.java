package ru.practicum.ewm.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.service.CategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
@Slf4j
@Validated
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public CategoryDto create(@RequestBody @Valid NewCategoryDto dto) {
        log.info("create in AdminCategoryController");
        return categoryService.create(dto);
    }

    @PatchMapping("/{categoryId}")
    public CategoryDto edit(@PathVariable Long categoryId,
            @RequestBody @Valid CategoryDto dto) {
        log.info("edit in AdminCategoryController categoryId = {}", categoryId);
        return categoryService.edit(categoryId, dto);
    }

    @DeleteMapping("/{categoryId}")
    public void delete(@PathVariable Long categoryId) {
        log.info("delete in AdminCategoryController categoryId = {}", categoryId);
        categoryService.delete(categoryId);
    }
}
