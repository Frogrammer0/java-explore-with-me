package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.common.exception.ConflictException;
import ru.practicum.ewm.common.exception.NotFoundException;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserMapper;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService{
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
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        if (ids == null || ids.isEmpty()) {
            log.info("получение всех пользователей в UserService");
            return userRepository.findAllWithOffset(from, size).stream()
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
