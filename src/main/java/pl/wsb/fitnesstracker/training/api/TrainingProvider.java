package pl.wsb.fitnesstracker.training.api;

import pl.wsb.fitnesstracker.training.internal.ActivityType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingProvider {

    /**
     * Retrieves a training based on their ID.
     * If the user with given ID is not found, then {@link Optional#empty()} will be returned.
     *
     * @param trainingId id of the training to be searched
     * @return An {@link Optional} containing the located Training, or {@link Optional#empty()} if not found
     */
    Optional<Training> getTraining(Long trainingId);

    /**
     * Retrieves all trainings.
     *
     * @return A list of all trainings
     */
    List<Training> findAllTrainings();

    /**
     * Retrieves all trainings for a specific user.
     *
     * @param userId id of the user whose trainings are to be retrieved
     * @return A list of trainings for the specified user
     */
    List<Training> findTrainingsByUserId(Long userId);

    /**
     * Retrieves all trainings that were finished after a specified date.
     *
     * @param afterTime the date after which trainings are to be retrieved
     * @return A list of trainings finished after the specified date
     */
    List<Training> findFinishedTrainingsAfter(LocalDate afterTime);

    /**
     * Retrieves all trainings of a specific activity type.
     *
     * @param activityType the type of activity for which trainings are to be retrieved
     * @return A list of trainings matching the specified activity type
     */
    List<Training> findTrainingsByActivityType(ActivityType activityType);

    /**
     * Creates a new training based on the provided request data.
     *
     * @param trainingRequestDto the data for the new training
     * @return The created Training object
     */
    Training createTraining(TrainingRequestDto trainingRequestDto);

    /**
     * Updates an existing training with the provided data.
     *
     * @param trainingId the ID of the training to be updated
     * @param trainingRequestDto the new data for the training
     * @return The updated Training object
     */
    Training updateTraining(Long trainingId, TrainingRequestDto trainingRequestDto);
}
