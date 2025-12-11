package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
@Validated
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto create(NewCategoryDto categoryDto) {
        log.info("создание категории в CategoryServiceImpl {}", categoryDto);
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new ConflictException("указана существующая категория");
        }
        Category category = categoryMapper.toCategory(categoryDto);
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto edit(Long categoryId, CategoryDto categoryDto) {
        log.info("изменение категории в CategoryServiceImpl {}", categoryDto);
        Category existCategory = getCategoryOrThrow(categoryId);
        if (categoryRepository.existsByName(categoryDto.getName()) &&
                !existCategory.getName().equals(categoryDto.getName())) {
            throw new ConflictException("неверно указана категория для обновления");
        }
        existCategory.setName(categoryDto.getName());
        return categoryMapper.toCategoryDto(categoryRepository.save(existCategory));
    }

    @Override
    public void delete(Long id) {
        log.info("удаление категории в CategoryServiceImpl с id = {}", id);
        if (eventRepository.existsByCategoryId(id)) {
            throw new ConflictException("указанная категория используется в событии");
        }
        getCategoryOrThrow(id);

        categoryRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryDto> getAllCategories(int from, int size) {
        log.info("получение категорий в CategoryServiceImpl с {}, в размере {}", from, size);
        Pageable page = PageRequest.of(from / size, size);
        return categoryRepository.findAll(page).stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryDto getCategoryById(Long id) {
        log.info("получение категории в CategoryServiceImpl с id = {}", id);
        return categoryMapper.toCategoryDto(getCategoryOrThrow(id));
    }

    private Category getCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(
                () -> new NotFoundException("не найдена указанная категория id = " + categoryId)
        );
    }
}
