package pl.wsb.fitnesstracker.user.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<User> findByEmailIgnoreCase(String email);

    Optional<User> findById(Long id);

    default List<User> findMatchingUser(UserSearch search) {
        return findAll().stream()
                .filter(user ->
                 (search.getFirstName() == null
                || Objects.equals(user.getFirstName(), search.getFirstName())) &&
                (search.getLastName() == null
                || Objects.equals(user.getLastName(), search.getLastName())) &&
                (search.getBirthDate() == null
                || user.getBirthDate().isEqual(search.getBirthDate())) &&
                (search.getEmail() == null
                || Objects.equals(user.getEmail(), search.getEmail()))
                )
                .collect(Collectors.toList());
    }

//    @Query("SELECT u FROM User u WHERE " +
//            "(:firstName IS NULL OR u.firstName = :firstName) AND " +
//            "(:lastName IS NULL OR u.lastName = :lastName) AND " +
//            "(:birthDate IS NULL OR u.birthDate = :birthDate) AND " +
//            "(:email IS NULL OR u.email = :email)")
//    List<User> findMatchingUser(@Param("firstName") String firstName,
//                                 @Param("lastName") String lastName,
//                                 @Param("birthDate") LocalDate birthDate,
//                                 @Param("email") String email);
    List<User> findAllByEmailContainingIgnoreCase(String partialEmail);
    List<User> findByBirthDateBefore(LocalDate date);
}