package pl.wsb.fitnesstracker.user.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.wsb.fitnesstracker.user.api.*;
import pl.wsb.fitnesstracker.user.exception.UserAlreadyExistsException;
import pl.wsb.fitnesstracker.user.exception.UserNotFoundException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
class UserServiceImpl implements UserService, UserProvider {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L} .'-]+$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PARTIAL_EMAIL_PATTERN = Pattern.compile("^[\\w.@-]+$");

    private final UserRepository userRepository;

    @Override
    public User createUser(final User user) throws UserAlreadyExistsException, IllegalArgumentException {
        validateNewUser(user);
        log.info("Creating User {}", user);
        if (user.getId() != null) {
            throw new IllegalArgumentException("User has already DB ID, update is not permitted!");
        }
        if (userRepository.findByEmailIgnoreCase(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with email already exists!");
        }
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUser(final Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<User> getUserByEmail(final String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserDetailsById(Long id) {
        if (id == null || id < 1) {
            log.error("Invalid id");
            throw new IllegalArgumentException("Invalid id");
        }
        log.info("Getting details for user's id: {}", id);
        return userRepository.findById(id);
    }
               /// //////////////
    @Override
    public Optional<User> getUserDetailsByEmail(String email) {
        if (null == email || email.isBlank()) {
            log.error("Invalid email");
            throw new IllegalArgumentException("Invalid email");
        }
        log.info("Getting details for user's email: {}", email);

        return userRepository.findByEmailIgnoreCase(email);
    }

    @Override
    public User deleteUserById(Long id) {
        List<User> users = userRepository.findAllById(Collections.singleton(id));
        if (users.isEmpty()) {
            throw new UserNotFoundException(id);
        }
        if (users.size() > 1) {
            throw new IllegalArgumentException("There is more than one user with id: " + id);
        }
        userRepository.delete(users.get(0));
        return users.get(0);
    }

    @Override
    public List<User> findMatchingUsers(UserSearch search) {
        validateSearch(search);

        log.info("Getting matching users by search: {}", search);
        return userRepository.findMatchingUser(search);
    }
    @Override
    public List<User> findMatchingUsersByPartialEmail(String partialEmail) throws IllegalArgumentException {
        validatePartialEmail(partialEmail);
        log.info("Getting matching users by email fragment: {}", partialEmail);
        return userRepository.findAllByEmailContainingIgnoreCase(partialEmail);
    }

    @Override
    public List<User> findUsersOlderThan(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("The date is required.");
        }
        log.info("Getting users older than: {}", date);
        return userRepository.findByBirthDateBefore(date);
    }

    @Override
    public User updateUser(Long id, User userToUpdate) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        validateUserToUpdate(userToUpdate);
        if (userToUpdate.getFirstName() != null) {
            existingUser.setFirstName(userToUpdate.getFirstName());
        }
        if (userToUpdate.getLastName() != null) {
            existingUser.setLastName(userToUpdate.getLastName());
        }
        if (userToUpdate.getBirthDate() != null) {
            existingUser.setBirthDate(userToUpdate.getBirthDate());
        }
        if (userToUpdate.getEmail() != null) {
            existingUser.setEmail(userToUpdate.getEmail());
        }

        return userRepository.save(existingUser);
    }

//============================= util methods ====================//

    private void validateSearch(UserSearch search) {
        if (search.getFirstName() != null && !NAME_PATTERN.matcher(search.getFirstName()).matches()) {
            throw new IllegalArgumentException("Invalid first name format: " + search.getFirstName());
        }

        if (search.getLastName() != null && !NAME_PATTERN.matcher(search.getLastName()).matches()) {
            throw new IllegalArgumentException("Invalid last name format: " + search.getLastName());
        }

        if (search.getEmail() != null && !EMAIL_PATTERN.matcher(search.getEmail()).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + search.getEmail());
        }

        if (search.getBirthDate() != null && search.getBirthDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Invalid Birthdate.");

        }
    }

    private void validateNewUser(User user) {
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("User cannot be null.");
        }

        if (user.getFirstName() == null || user.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name is required.");
        }
        if (!NAME_PATTERN.matcher(user.getFirstName()).matches()) {
            throw new IllegalArgumentException("First name contains invalid characters.");
        }

        if (user.getLastName() == null || user.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name is required.");
        }
        if (!NAME_PATTERN.matcher(user.getLastName()).matches()) {
            throw new IllegalArgumentException("Last name contains invalid characters.");
        }

        if (user.getBirthDate() == null) {
            throw new IllegalArgumentException("Birthdate is required.");
        }
        if (user.getBirthDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birthdate must be a date in the past.");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }
        if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            throw new IllegalArgumentException("Invalid email format.");
        }
    }

    private void validatePartialEmail(String partialEmail) {
        if (partialEmail == null || partialEmail.isBlank()) {
            throw new IllegalArgumentException("Partial email is required.");
        }
        if (!PARTIAL_EMAIL_PATTERN.matcher(partialEmail).matches()) {
            throw new IllegalArgumentException("Invalid partial email format.");
        }
    }

    private void validateUserToUpdate(User user) {
        if (user.getFirstName() != null && !NAME_PATTERN.matcher(user.getFirstName()).matches()) {
            throw new IllegalArgumentException("Invalid first name format: " + user.getFirstName());
        }

        if (user.getLastName() != null && !NAME_PATTERN.matcher(user.getLastName()).matches()) {
            throw new IllegalArgumentException("Invalid last name format: " + user.getLastName());
        }

        if (user.getEmail() != null && !EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + user.getEmail());
        }
        if (user.getBirthDate() != null && user.getBirthDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Invalid Birthdate.");
        }
    }
}