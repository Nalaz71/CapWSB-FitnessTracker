package pl.wsb.fitnesstracker.user.internal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.api.UserDto;
import pl.wsb.fitnesstracker.user.exception.UserNotFoundException;
import pl.wsb.fitnesstracker.user.api.UserSearch;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
class UserController {

    private final UserServiceImpl userService;
    private final UserMapper userMapper;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDto> getAllUsers() {
        return userService.findAllUsers()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @GetMapping(value = "/simple", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserSimpleDto> getSimpleDataForAllUsers() {
        return userService.findAllUsers()
                .stream()
                .map(userMapper::toSimpleDto)
                .toList();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@Valid @RequestBody UserDto userDto) {
        System.out.println("User with e-mail: " + userDto.email() + " passed to the request");
        return userService.createUser(userMapper.toEntity(userDto));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto getUserDetailsById(@PathVariable Long id) {
        return userService.getUserDetailsById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @GetMapping(value = "/email", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDto> getUserDetailsByEmail(@RequestParam String email) {
        return userService.getUserDetailsByEmail(email)
                .map(userMapper::toDto)
                .map(Collections::singletonList)
                .orElse(Collections.emptyList());
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
    }

    @GetMapping(value = "/partialEmail", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDto> getUserDetailsByPartialEmail(@RequestParam String partialEmail) {
        return userService.findMatchingUsersByPartialEmail(partialEmail)
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/olderThan", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDto> getUsersOlderThan(@PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return userService.findUsersOlderThan(date)
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping(value = "/matchingUsers", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDto> findMatchingUsers(@Valid @RequestBody UserSearch userSearch) {
        return userService.findMatchingUsers(userSearch)
                .stream()
                .map(userMapper::toDtoJustEmailAndId)
                .collect(Collectors.toList());
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto) {
        return userService.updateUser(id, userMapper.toEntity(userDto));
    }


    }




}