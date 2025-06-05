package pl.wsb.fitnesstracker.training.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.wsb.fitnesstracker.training.api.Training;
import pl.wsb.fitnesstracker.training.api.TrainingNotFoundException;
import pl.wsb.fitnesstracker.training.api.TrainingProvider;
import pl.wsb.fitnesstracker.training.api.TrainingRequestDto;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.api.UserNotFoundException;
import pl.wsb.fitnesstracker.user.api.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the TrainingProvider interface.
 * This service provides methods to manage training sessions, including creating, updating, and retrieving trainings.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingServiceImpl implements TrainingProvider {

    private final TrainingRepository trainingRepository;
    private final UserService userService;

    /**
     * Retrieves a training session by its ID.
     *
     * @param trainingId the ID of the training session to retrieve
     * @return an Optional containing the Training if found, or empty if not found
     */
    @Override
    public Optional<Training> getTraining(final Long trainingId) {
        return trainingRepository.findById(trainingId);
    }

    /**
     * Retrieves all training sessions.
     *
     * @return a list of all Training sessions
     */
    @Override
    public List<Training> findAllTrainings() {
        log.info("Getting all trainings.");

        return trainingRepository.findAll();
    }

    /**
     * Retrieves all training sessions for a specific user.
     *
     * @param userId the ID of the user whose training sessions are to be retrieved
     * @return a list of Training sessions for the specified user
     */
    @Override
    public List<Training> findTrainingsByUserId(Long userId) {
        log.info("Getting all trainings for the user with the id: {}", userId);

        return trainingRepository.findByUserId(userId);
    }

    /**
     * Retrieves all training sessions that were finished after a specified date.
     *
     * @param afterTime the date after which training sessions are to be retrieved
     * @return a list of Training sessions finished after the specified date
     */
    @Override
    public List<Training> findFinishedTrainingsAfter(LocalDate afterTime) {
        log.info("Getting all trainings finished after: {}",
                afterTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        java.sql.Date endTime = java.sql.Date.valueOf(afterTime);
        return trainingRepository.findByEndTimeAfter(endTime);
    }

    /**
     * Retrieves all training sessions of a specific activity type.
     *
     * @param activityType the type of activity for which training sessions are to be retrieved
     * @return a list of Training sessions matching the specified activity type
     */
    @Override
    public List<Training> findTrainingsByActivityType(ActivityType activityType) {
        return trainingRepository.findByActivityType(activityType);
    }

    /**
     * Creates a new training session based on the provided request data.
     *
     * @param trainingRequestDto the data for the new training session
     * @return the created Training object
     */
    @Override
    public Training createTraining(TrainingRequestDto trainingRequestDto) {
        Optional<User> optionalUser = userService.getUserDetailsById(trainingRequestDto.getUserId());
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(trainingRequestDto.getUserId());
        }

        Training training = new Training(optionalUser.get(),
                convertToDate(trainingRequestDto.getStartTime()),
                convertToDate(trainingRequestDto.getEndTime()),
                trainingRequestDto.getActivityType(),
                trainingRequestDto.getDistance(),
                trainingRequestDto.getAverageSpeed());

        return trainingRepository.save(training);
    }

    /**
     * Updates an existing training session with the provided data.
     *
     * @param trainingId the ID of the training session to be updated
     * @param trainingRequestDto the new data for the training session
     * @return the updated Training object
     */
    public Training updateTraining(Long trainingId, TrainingRequestDto trainingRequestDto) {
        Optional<Training> optionalTraining = getTraining(trainingId);
        if (optionalTraining.isEmpty()) {
            throw new TrainingNotFoundException(trainingId);
        }

        Training existingTraining = optionalTraining.get();

        Optional<User> optionalUser = userService.getUserDetailsById(trainingRequestDto.getUserId());
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(trainingRequestDto.getUserId());
        }
        User existingUser = optionalUser.get();

        if (trainingRequestDto.getUserId() != null) {
            existingTraining.setUser(existingUser);
        }
        if (trainingRequestDto.getStartTime() != null) {
            existingTraining.setStartTime(convertToDate(trainingRequestDto.getStartTime()));
        }
        if (trainingRequestDto.getEndTime() != null) {
            existingTraining.setEndTime(convertToDate(trainingRequestDto.getEndTime()));
        }
        if (trainingRequestDto.getActivityType() != null) {
            existingTraining.setActivityType(trainingRequestDto.getActivityType());
        }
        if (trainingRequestDto.getDistance() > 0) {
            existingTraining.setDistance(trainingRequestDto.getDistance());
        }
        if (trainingRequestDto.getAverageSpeed() > 0) {
            existingTraining.setAverageSpeed(trainingRequestDto.getAverageSpeed());
        }

        return trainingRepository.save(existingTraining);  // Zapisanie zaktualizowanego treningu
    }

//    ======================== util methods ========================

    /**
     * Converts a LocalDateTime to a java.util.Date.
     *
     * @param localDateTime the LocalDateTime to convert
     * @return the converted java.util.Date
     */
    public static java.util.Date convertToDate(LocalDateTime localDateTime) {
        return java.sql.Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}



