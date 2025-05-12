package pl.wsb.fitnesstracker.user.api;

import pl.wsb.fitnesstracker.user.exception.UserNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserService {

    User createUser(User user) throws IllegalArgumentException, UserNotFoundException;

    Optional<User> getUserDetailsById(Long id);

    Optional<User> getUserDetailsByEmail(String email);

    List<User> findMatchingUsers(UserSearch search);

    List<User> findUsersOlderThan(LocalDate date);

    User updateUser(Long id, User user);

    User deleteUserById(Long id);

    List<User> findMatchingUsersByPartialEmail(String partialEmail) throws IllegalArgumentException;

}
