package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.service.UserService;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.dto.user.NewUserRequest;
import ru.practicum.ewm.dto.user.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Override
    public UserDto create(NewUserRequest userDto) {
        log.info("создание пользователя в UserService {}", userDto);
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new ConflictException("указан существующий имейл");
        }
        User user = userMapper.toUser(userDto);
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        if (ids == null || ids.isEmpty()) {
            log.info("получение всех пользователей в UserService");
            Pageable page = PageRequest.of(from / size, size);
            return userRepository.findAll(page).stream()
                    .map(userMapper::toUserDto)
                    .collect(Collectors.toList());
        }
        log.info("получение пользователей из списка id = {}", ids);
        return userRepository.findByIdIn(ids, from, size).stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("пользователь не найден");
        }
        log.info("удаление пользователя с id = {}", id);
        userRepository.deleteById(id);
    }
}
