package pl.wsb.fitnesstracker.training.internal;


import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pl.wsb.fitnesstracker.training.api.Training;
import pl.wsb.fitnesstracker.training.api.TrainingDto;
import pl.wsb.fitnesstracker.training.api.TrainingRequestDto;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Controller for managing training sessions.
 * Provides endpoints to create, update, and retrieve training sessions.
 */
@RestController
@RequestMapping("/v1/trainings")
@RequiredArgsConstructor
public class TrainingController {

    public final TrainingServiceImpl trainingService;
    public final TrainingMapper trainingMapper;

    /**
     * Retrieves all training sessions.
     *
     * @return a list of TrainingDto representing all training sessions.
     */
    @GetMapping
    public List<TrainingDto> getAllTraining() {
        return trainingService.findAllTrainings()
                .stream()
                .map(trainingMapper::toDto)
                .toList();
    }

    /**
     * Retrieves training sessions for a specific user.
     *
     * @param userId the ID of the user whose training sessions are to be retrieved.
     * @return a list of TrainingDto representing the user's training sessions.
     */
    @GetMapping("/{userId}")
    public List<TrainingDto> getTrainingsForUser(@PathVariable Long userId) {
        return trainingService.findTrainingsByUserId(userId)
                .stream()
                .map(trainingMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves training sessions that were finished after a specified date.
     *
     * @param afterTime the date after which training sessions are to be retrieved.
     * @return a list of TrainingDto representing the training sessions finished after the specified date.
     */
    @GetMapping("/finished/{afterTime}")
    public List<TrainingDto> getFinishedTrainingsAfter(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate afterTime) {
        return trainingService.findFinishedTrainingsAfter(afterTime)
                .stream()
                .map(trainingMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves training sessions by activity type.
     *
     * @param activityType the type of activity for which training sessions are to be retrieved.
     * @return a list of TrainingDto representing the training sessions of the specified activity type.
     */
    @GetMapping("/activityType")
    public List<TrainingDto> getTraningsByActivityType(@RequestParam ActivityType activityType) {
        return trainingService.findTrainingsByActivityType(activityType)
                .stream()
                .map(trainingMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new training session.
     *
     * @param trainingRequestDto the data for the new training session.
     * @return a TrainingDto representing the created training session.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrainingDto createTraining(@RequestBody TrainingRequestDto trainingRequestDto) {
        Training training = trainingService.createTraining(trainingRequestDto);
        return trainingMapper.toDto(training);
    }

    /**
     * Updates an existing training session.
     *
     * @param trainingId the ID of the training session to be updated.
     * @param trainingRequestDto the new data for the training session.
     * @return a TrainingDto representing the updated training session.
     */
    @PutMapping("/{trainingId}")
    public TrainingDto updateTraining(
            @PathVariable Long trainingId,
            @RequestBody TrainingRequestDto trainingRequestDto) {
        Training updatedTraining = trainingService.updateTraining(trainingId, trainingRequestDto);
        return trainingMapper.toDto(updatedTraining);
    }

}