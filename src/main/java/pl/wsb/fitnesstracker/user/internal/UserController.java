package pl.wsb.fitnesstracker.user.internal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.api.UserDto;
import pl.wsb.fitnesstracker.user.api.UserNotFoundException;
import pl.wsb.fitnesstracker.user.api.UserSearch;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing user-related operations.
 * Provides endpoints for creating, retrieving, updating, and deleting users.
 */
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
class UserController {

    private final UserServiceImpl userService;
    private final UserMapper userMapper;

    /**
     * Retrieves all users and returns them as a list of UserDto objects.
     *
     * @return a list of all users in the system
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDto> getAllUsers() {
        return userService.findAllUsers()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    /**
     * Retrieves simplified data for all users.
     *
     * @return a list of UserSimpleDto with basic user data
     */
    @GetMapping(value = "/simple", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserSimpleDto> getSimpleDataForAllUsers() {
        return userService.findAllUsers()
                .stream()
                .map(userMapper::toSimpleDto)
                .toList();
    }

    /**
     * Adds a new user based on the provided UserDto data.
     *
     * @param userDto the user data transfer object with information to create a new user
     * @return the newly created User entity
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@Valid @RequestBody UserDto userDto) {
        System.out.println("User with e-mail: " + userDto.email() + " passed to the request");

        return userService.createUser(userMapper.toEntity(userDto));
    }

    /**
     * Retrieves detailed information of a user by their ID.
     *
     * @param id the ID of the user to retrieve
     * @return the UserDto object containing user details
     * @throws UserNotFoundException if the user with the specified ID is not found
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto getUserDetailsById(@PathVariable Long id) {
        return userService.getUserDetailsById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

     /**
     * Retrieves detailed information of a user by their email.
     *
     * @param email the email of the user to retrieve
     * @return a list containing the UserDto if found, or an empty list if not found
     */
    @GetMapping(value = "/email", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDto> getUserDetailsByEmail(@RequestParam String email) {
        return userService.getUserDetailsByEmail(email)
                .map(userMapper::toDto)
                .map(Collections::singletonList)
                .orElse(Collections.emptyList());
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public User deleteUserById(@PathVariable Long id) {
       return userService.deleteUserById(id);
    }

    /**
     * Retrieves detailed information of users whose email matches the provided partial email.
     *
     * @param partialEmail the partial email to search for
     * @return a list of UserDto objects matching the partial email
     */
    @GetMapping(value = "/partialEmail", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDto> getUserDetailsByPartialEmail(@RequestParam String partialEmail) {
        return userService.findMatchingUsersByPartialEmail(partialEmail)
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves users older than the specified date.
     *
     * @param date the date to compare against
     * @return a list of UserDto objects of users older than the specified date
     */
    @GetMapping(value = "/older/{date}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDto> getUsersOlderThan(@PathVariable("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return userService.findUsersOlderThan(date)
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves users who match the criteria specified in the UserSearch object.
     *
     * @param userSearch the search criteria for finding matching users
     * @return a list of UserDto objects of users matching the search criteria
     */
    @PostMapping(value = "/matchingUsers", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDto> findMatchingUser(@Valid @RequestBody UserSearch userSearch) {
        return userService.findMatchingUsers(userSearch)
                .stream()
                .map(userMapper::toDtoJustEmailAndId)
                .collect(Collectors.toList());
    }

    /**
     * Updates the details of an existing user.
     *
     * @param id       the ID of the user to update
     * @param userDto  the UserDto object containing updated user information
     * @return the updated User entity
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public User updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        return userService.updateUser(id, userMapper.toEntity(userDto));
    }
}
