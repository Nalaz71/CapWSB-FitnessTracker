package pl.wsb.fitnesstracker.user.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.wsb.fitnesstracker.user.api.*;
import pl.wsb.fitnesstracker.user.api.UserNotFoundException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Implementation of the UserService interface.
 * This class provides methods to manage users, including creating, updating, deleting, and searching for users.
 */
@Service
@RequiredArgsConstructor
@Slf4j
class UserServiceImpl implements UserService, UserProvider {

    /**
     * Regular expression patterns for validating user input.
     */
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L} .'-]+$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PARTIAL_EMAIL_PATTERN = Pattern.compile("^[\\w.@-]+$");

    private final UserRepository userRepository;

    /**
     * Creates a new user after validating the provided user details.
     *
     * @param user the user entity to be created
     * @return the created user entity
     * @throws IllegalArgumentException if the user details are invalid or the user already has an ID
     */
    @Override
    public User createUser(final User user) throws IllegalArgumentException, UserNotFoundException {
        validateNewUser(user);

        log.info("Creating User {}", user);
        if (user.getId() != null) {
            throw new IllegalArgumentException("User has already DB ID, update is not permitted!");
        }
        return userRepository.save(user);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId the ID of the user to retrieve
     * @return an Optional containing the user if found, or empty if not found
     */
    @Override
    public Optional<User> getUser(final Long userId) {
        return userRepository.findById(userId);
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email the email address of the user to retrieve
     * @return an Optional containing the user if found, or empty if not found
     */
    @Override
    public Optional<User> getUserByEmail(final String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Retrieves all users from the database.
     *
     * @return a list of all users
     */
    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve
     * @return an Optional containing the user if found, or empty if not found
     * @throws IllegalArgumentException if the ID is invalid
     */
    @Override
    public Optional<User> getUserDetailsById(Long id) {
        if (id == null || id < 1) {
            log.error("Invalid id");
            throw new IllegalArgumentException("Invalid id");
        }
        log.info("Getting details for user's id: {}", id);

        return userRepository.findById(id);
    }

    /**
     * Retrieves a user by their email address, ignoring case.
     *
     * @param email the email address of the user to retrieve
     * @return an Optional containing the user if found, or empty if not found
     * @throws IllegalArgumentException if the email is invalid
     */
    @Override
    public Optional<User> getUserDetailsByEmail(String email) {
        if (null == email || email.isBlank()) {
            log.error("Invalid email");
            throw new IllegalArgumentException("Invalid email");
        }
        log.info("Getting details for user's email: {}", email);

        return userRepository.findByEmail(email);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     * @return the deleted user entity
     * @throws UserNotFoundException if the user with the specified ID is not found
     * @throws IllegalArgumentException if there is more than one user with the specified ID
     */
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

    /**
     * Finds users matching specific search criteria.
     *
     * @param search the search criteria to match users
     * @return a list of users that match the search criteria
     * @throws IllegalArgumentException if any search criteria are invalid
     */
    @Override
    public List<User> findMatchingUsers(UserSearch search) {
        validateSearch(search);
        log.info("Getting matching users by search: {}", search);

        return userRepository.findMatchingUser(search);
    }

    /**
     * Finds users matching a partial email address.
     *
     * @param partialEmail the partial email address to match users
     * @return a list of users that match the partial email address
     * @throws IllegalArgumentException if the partial email is invalid
     */
    @Override
    public List<User> findMatchingUsersByPartialEmail(String partialEmail) throws IllegalArgumentException {
        validatePartialEmail(partialEmail);
        log.info("Getting matching users by email fragment: {}", partialEmail);

        return userRepository.findAllByEmailContainingIgnoreCase(partialEmail);
    }

    /**
     * Finds users older than a specific date.
     *
     * @param date the date to compare users' birthdates
     * @return a list of users older than the specified date
     * @throws IllegalArgumentException if the date is null
     */
    @Override
    public List<User> findUsersOlderThan(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("The date is required.");
        }
        log.info("Getting users older than: {}", date);

        return userRepository.findByBirthdateBefore(date);
    }

    /**
     * Updates an existing user with new details.
     *
     * @param id the ID of the user to update
     * @param userToUpdate the new user details
     * @return the updated user entity
     * @throws UserNotFoundException if the user with the specified ID is not found
     * @throws IllegalArgumentException if the user details are invalid
     */
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
        if (userToUpdate.getBirthdate() != null) {
            existingUser.setBirthdate(userToUpdate.getBirthdate());
        }
        if (userToUpdate.getEmail() != null) {
            existingUser.setEmail(userToUpdate.getEmail());
        }

        return userRepository.save(existingUser);
    }

//============================= utility methods=========================//

    /**
     * Validates the search criteria for finding users.
     *
     * @param search the search criteria to validate
     * @throws IllegalArgumentException if any search criteria are invalid
     */
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

        if (search.getBirthdate() != null && search.getBirthdate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Invalid Birthdate.");
        }
    }

    /**
     * Validates the provided user details for creating a new user.
     *
     * @param user the user entity to validate
     * @throws IllegalArgumentException if the user details are invalid
     */
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

        if (user.getBirthdate() == null) {
            throw new IllegalArgumentException("Birthdate is required.");
        }
        if (user.getBirthdate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birthdate must be a date in the past.");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }
        if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            throw new IllegalArgumentException("Invalid email format.");
        }
    }

    /**
     * Validates the provided partial email address.
     *
     * @param partialEmail the partial email address to validate
     * @throws IllegalArgumentException if the partial email is invalid
     */
    private void validatePartialEmail(String partialEmail) {
        if (partialEmail == null || partialEmail.isBlank()) {
            throw new IllegalArgumentException("Partial email is required.");
        }
        if (!PARTIAL_EMAIL_PATTERN.matcher(partialEmail).matches()) {
            throw new IllegalArgumentException("Invalid partial email format.");
        }
    }

    /**
     * Validates the provided user details for updating an existing user.
     *
     * @param user the user entity to validate
     * @throws IllegalArgumentException if the user details are invalid
     */
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
        if (user.getBirthdate() != null && user.getBirthdate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Invalid Birthdate.");
        }
    }

}