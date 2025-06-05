package pl.wsb.fitnesstracker.training.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.wsb.fitnesstracker.training.api.Training;
import pl.wsb.fitnesstracker.training.api.TrainingDto;

/**
 * Mapper class for converting Training entities to Training DTOs.
 * This class is responsible for mapping the internal Training entity to the API TrainingDto.
 */
@Component
@RequiredArgsConstructor
public class TrainingMapper {

    /**
     * Converts a Training entity to a TrainingDto.
     *
     * @param training the Training entity to convert
     * @return the converted TrainingDto
     */
    public TrainingDto toDto(Training training) {
        return new TrainingDto(
                training.getUser(),
                training.getStartTime(),
                training.getEndTime(),
                training.getActivityType(),
                training.getDistance(),
                training.getAverageSpeed()
        );
    }

}