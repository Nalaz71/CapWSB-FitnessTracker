package pl.wsb.fitnesstracker.user.exception;

import pl.wsb.fitnesstracker.exception.api.NotFoundException;
import pl.wsb.fitnesstracker.user.api.User;

/**
 * Exception indicating that the {@link User} was not found.
 */
@SuppressWarnings("squid:S110")
public class UserNotFoundException extends NotFoundException {

    private UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(Long id) {
        this("User with ID=%s was not found".formatted(id));
    }
}
