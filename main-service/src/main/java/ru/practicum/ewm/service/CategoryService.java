package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto create(NewCategoryDto categoryDto);

    CategoryDto edit(Long CategoryId, CategoryDto categoryDto);

    void delete(Long id);

    List<CategoryDto> getAllCategories(int from, int size);

    CategoryDto getCategoryById(Long id);
}
