package pl.wsb.fitnesstracker.user.internal;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.api.UserDto;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void toDtoShouldMapAllFieldsWhenUserIsValid() {
        // Given
        User user = new User("John", "Doe", LocalDate.of(1990, 1, 1), "john.doe@example.com");
        user.setId(1L);

        // When
        UserDto userDto = userMapper.toDto(user);

        // Then
        assertEquals(1L, userDto.id());
        assertEquals("John", userDto.firstName());
        assertEquals("Doe", userDto.lastName());
        assertEquals(LocalDate.of(1990, 1, 1), userDto.birthdate());
        assertEquals("john.doe@example.com", userDto.email());
    }

    @Test
    void toDtoJustEmailAndIdShouldMapOnlyEmailAndId() {
        // Given
        User user = new User("John", "Doe", LocalDate.of(1990, 1, 1), "john.doe@example.com");
        user.setId(1L);

        // When
        UserDto userDto = userMapper.toDtoJustEmailAndId(user);

        // Then
        assertEquals(1L, userDto.id());
        assertNull(userDto.firstName());
        assertNull(userDto.lastName());
        assertNull(userDto.birthdate());
        assertEquals("john.doe@example.com", userDto.email());
    }

    @Test
    void toSimpleDtoShouldMapIdAndName() {
        // Given
        User user = new User("John", "Doe", LocalDate.of(1990, 1, 1), "john.doe@example.com");
        user.setId(1L);

        // When
        UserSimpleDto userSimpleDto = userMapper.toSimpleDto(user);

        // Then
        assertEquals(1L, userSimpleDto.id());
        assertEquals("John", userSimpleDto.firstName());
        assertEquals("Doe", userSimpleDto.lastName());
    }

    @Test
    void toEntityShouldMapAllFields() {
        // Given
        UserDto userDto = new UserDto(1L, "John", "Doe", LocalDate.of(1990, 1, 1), "john.doe@example.com");

        // When
        User user = userMapper.toEntity(userDto);

        // Then
        assertNull(user.getId());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), user.getBirthdate());
        assertEquals("john.doe@example.com", user.getEmail());
    }

    @Test
    void toDtoShouldThrowExceptionWhenUserIsNull() {
        // when & then
        assertThrows(NullPointerException.class, () -> userMapper.toDto(null));
    }

    @Test
    void toDtoJustEmailAndIdShouldThrowExceptionWhenUserIsNull() {
        // when & then
        assertThrows(NullPointerException.class, () -> userMapper.toDtoJustEmailAndId(null));
    }

    @Test
    void toSimpleDtoShouldThrowExceptionWhenUserIsNull() {
        // when & then
        assertThrows(NullPointerException.class, () -> userMapper.toSimpleDto(null));
    }

    @Test
    void toEntityShouldThrowExceptionWhenDtoIsNull() {
        // when & then
        assertThrows(NullPointerException.class, () -> userMapper.toEntity(null));
    }

}
