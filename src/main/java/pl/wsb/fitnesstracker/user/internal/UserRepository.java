package pl.wsb.fitnesstracker.user.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.api.UserSearch;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Repository
interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Query searching users by email address. It matches by exact match.
     *
     * @param email email of the user to search
     * @return {@link Optional} containing found user or {@link Optional#empty()} if none matched
     */
    default Optional<User> findByEmail(String email) {
        return findAll().stream()
                .filter(user -> Objects.equals(user.getEmail().toLowerCase(), email.toLowerCase()))
                .findFirst();
    }

    /**
     * Query searching users by id. It matches by exact match.
     *
     * @param id id of the user to search
     * @return {@link Optional} containing found user or {@link Optional#empty()} if none matched
     */
    default Optional<User> findById(Long id) {
        return findAll().stream()
                .filter(user -> (Objects.equals(user.getId(), id)))
                .findFirst();
    }

    /**
     * Query searching users by search criteria. It matches by exact match.
     *
     * @param search search criteria
     * @return {@link List} containing found users or empty list if none matched
     */
    default List<User> findMatchingUser(UserSearch search) {
        return findAll().stream()
                .filter(user ->
                 (search.getFirstName() == null
                || Objects.equals(user.getFirstName(), search.getFirstName())) &&
                (search.getLastName() == null
                || Objects.equals(user.getLastName(), search.getLastName())) &&
                (search.getBirthdate() == null
                || user.getBirthdate().isEqual(search.getBirthdate())) &&
                (search.getEmail() == null
                || Objects.equals(user.getEmail(), search.getEmail()))
                )
                .collect(Collectors.toList());
    }

    /**
     * Query searching users by email address. It matches by partial match.
     *
     * @param partialEmail partial email of the user to search
     * @return {@link List} containing found users or empty list if none matched
     */
    List<User> findAllByEmailContainingIgnoreCase(String partialEmail);

    /**
     * Query searching users by birthdate. It matches by date before the given date.
     *
     * @param date date to search
     * @return {@link List} containing found users or empty list if none matched
     */
    List<User> findByBirthdateBefore(LocalDate date);
}