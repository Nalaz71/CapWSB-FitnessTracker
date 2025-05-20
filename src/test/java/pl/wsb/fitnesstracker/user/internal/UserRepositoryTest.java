package pl.wsb.fitnesstracker.user.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.api.UserSearch;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    public UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        user1 = new User("John", "Doe", LocalDate.of(1990, 1, 1), "john.doe@example.com");
        user2 = new User("Jane", "Smith", LocalDate.of(1995, 5, 5), "jane.smith@example.com");

        userRepository.save(user1);
        userRepository.save(user2);
    }

    @Test
    void FindByEmailIgnoreCaseShouldReturnUserWhenEmailIgnoreCase() {
        //when
        Optional<User> result = userRepository.findByEmail("JOHN.DOE@EXAMPLE.COM");

        //then
        assertThat(result).isPresent();
        assertEquals("john.doe@example.com", result.get().getEmail());
        assertEquals("John", result.get().getFirstName());
    }

    @Test
    void findMatchingUserShouldReturnUsersWhenAllCriteriaMatch() {
        // given
        UserSearch userSearch = new UserSearch("John", "Doe", LocalDate.of(1990, 1, 1), "john.doe@example.com");

        // when
        List<User> result = userRepository.findMatchingUser(userSearch);

        // then
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("john.doe@example.com", result.get(0).getEmail());
    }
}