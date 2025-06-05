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

// TODO: Provide Implementation and correct the return type of the method getTraining
@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingServiceImpl implements TrainingProvider {

    private final TrainingRepository trainingRepository;
    private final UserService userService;

    @Override
    public Optional<Training> getTraining(final Long trainingId) {
        return trainingRepository.findById(trainingId);
    }

    @Override
    public List<Training> findAllTrainings() {
        log.info("Getting all trainings.");

        return trainingRepository.findAll();
    }

    @Override
    public List<Training> findTrainingsByUserId(Long userId) {
        log.info("Getting all trainings for the user with the id: {}", userId);

        return trainingRepository.findByUserId(userId);
    }

    @Override
    public List<Training> findFinishedTrainingsAfter(LocalDate afterTime) {
        log.info("Getting all trainings finished after: {}",
                afterTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        java.sql.Date endTime = java.sql.Date.valueOf(afterTime);
        return trainingRepository.findByEndTimeAfter(endTime);
    }

    @Override
    public List<Training> findTrainingsByActivityType(ActivityType activityType) {
        return trainingRepository.findByActivityType(activityType);
    }

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

    public static java.util.Date convertToDate(LocalDateTime localDateTime) {
        return java.sql.Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}



