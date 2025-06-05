package pl.wsb.fitnesstracker.training.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.wsb.fitnesstracker.training.api.Training;

import java.util.Date;
import java.util.List;

/**
 * Repository interface for managing Training entities.
 * This interface extends JpaRepository to provide CRUD operations for Training entities.
 */
interface TrainingRepository extends JpaRepository<Training, Long> {

        /**
         * Finds all Training entities associated with a specific user ID.
         *
         * @param userId the ID of the user whose trainings are to be retrieved
         * @return a list of Training entities associated with the specified user ID
         */
        List<Training> findByUserId(Long userId);

        /**
         * Finds all Training entities that were finished after a specified end time.
         *
         * @param endTime the date after which trainings are to be retrieved
         * @return a list of Training entities that were finished after the specified end time
         */
        List<Training> findByEndTimeAfter(Date endTime);

        /**
         * Finds all Training entities of a specific activity type.
         *
         * @param activityType the type of activity for which trainings are to be retrieved
         * @return a list of Training entities matching the specified activity type
         */
        List<Training> findByActivityType(ActivityType activityType);
    }

