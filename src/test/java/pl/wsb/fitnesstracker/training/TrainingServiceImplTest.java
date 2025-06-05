package pl.wsb.fitnesstracker.training;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pl.wsb.fitnesstracker.training.api.Training;
import pl.wsb.fitnesstracker.training.api.TrainingNotFoundException;
import pl.wsb.fitnesstracker.training.api.TrainingRequestDto;
import pl.wsb.fitnesstracker.training.internal.ActivityType;
import pl.wsb.fitnesstracker.training.internal.TrainingRepository;
import pl.wsb.fitnesstracker.training.internal.TrainingServiceImpl;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.api.UserNotFoundException;
import pl.wsb.fitnesstracker.user.api.UserService;


import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TrainingServiceImplTest {
    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private TrainingServiceImpl trainingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnTrainingById() {
        Training training = mock(Training.class);
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));
        Optional<Training> result = trainingService.getTraining(1L);
        assertTrue(result.isPresent());
        assertEquals(training, result.get());
    }

    @Test
    void shouldReturnAllTrainings() {
        List<Training> trainings = Arrays.asList(mock(Training.class), mock(Training.class));
        when(trainingRepository.findAll()).thenReturn(trainings);
        List<Training> result = trainingService.findAllTrainings();
        assertEquals(2, result.size());
    }

    @Test
    void shouldCreateTraining() {
        User user = mock(User.class);
        Training training = mock(Training.class);
        TrainingRequestDto dto = new TrainingRequestDto();
        dto.setUserId(1L);
        dto.setStartTime(LocalDateTime.now());
        dto.setEndTime(LocalDateTime.now());
        dto.setActivityType(ActivityType.RUNNING);
        dto.setDistance(10.0);
        dto.setAverageSpeed(5.0);
        when(userService.getUserDetailsById(1L)).thenReturn(Optional.of(user));
        when(trainingRepository.save(any(Training.class))).thenReturn(training);
        Training result = trainingService.createTraining(dto);
        assertNotNull(result);
    }

    @Test
    void shouldThrowWhenUserNotFoundOnCreate() {
        TrainingRequestDto dto = new TrainingRequestDto();
        dto.setUserId(99L);
        dto.setStartTime(LocalDateTime.now());
        dto.setEndTime(LocalDateTime.now());
        dto.setActivityType(ActivityType.RUNNING);
        dto.setDistance(10.0);
        dto.setAverageSpeed(5.0);
        when(userService.getUserDetailsById(99L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> trainingService.createTraining(dto));
    }

    @Test
    void shouldUpdateTraining() {
        User user = mock(User.class);
        Training training = mock(Training.class);
        TrainingRequestDto dto = new TrainingRequestDto();
        dto.setUserId(1L);
        dto.setStartTime(LocalDateTime.now());
        dto.setEndTime(LocalDateTime.now());
        dto.setActivityType(ActivityType.RUNNING);
        dto.setDistance(10.0);
        dto.setAverageSpeed(5.0);
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));
        when(userService.getUserDetailsById(1L)).thenReturn(Optional.of(user));
        when(trainingRepository.save(any(Training.class))).thenReturn(training);
        Training result = trainingService.updateTraining(1L, dto);
        assertNotNull(result);
    }

    @Test
    void shouldThrowWhenTrainingNotFoundOnUpdate() {
        TrainingRequestDto dto = new TrainingRequestDto();
        dto.setUserId(1L);
        dto.setStartTime(LocalDateTime.now());
        dto.setEndTime(LocalDateTime.now());
        dto.setActivityType(ActivityType.RUNNING);
        dto.setDistance(10.0);
        dto.setAverageSpeed(5.0);
        when(trainingRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(TrainingNotFoundException.class, () -> trainingService.updateTraining(1L, dto));
    }

    @Test
    void shouldThrowWhenUserNotFoundOnUpdate() {
        Training training = mock(Training.class);
        TrainingRequestDto dto = new TrainingRequestDto();
        dto.setUserId(99L);
        dto.setStartTime(LocalDateTime.now());
        dto.setEndTime(LocalDateTime.now());
        dto.setActivityType(ActivityType.RUNNING);
        dto.setDistance(10.0);
        dto.setAverageSpeed(5.0);
        when(trainingRepository.findById(1L)).thenReturn(Optional.of(training));
        when(userService.getUserDetailsById(99L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> trainingService.updateTraining(1L, dto));
    }
}
