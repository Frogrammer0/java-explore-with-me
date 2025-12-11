package ru.practicum.ewm.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.user.NewUserRequest;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
@Slf4j
public class AdminUserController {

    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody @Valid NewUserRequest dto) {
        log.info("create in AdminUserController");
        return userService.create(dto);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam List<Long> ids,
                                  @RequestParam int from,
                                  @RequestParam int size) {
        log.info("getUsers in AdminUserController");
        return userService.getUsers(ids, from, size);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("delete in AdminUserController");
        userService.delete(userId);
    }

}
