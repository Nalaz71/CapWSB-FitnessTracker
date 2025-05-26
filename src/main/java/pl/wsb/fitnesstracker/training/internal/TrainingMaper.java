package pl.wsb.fitnesstracker.training.internal;

import org.springframework.stereotype.Component;
import pl.wsb.fitnesstracker.training.api.Training;
import pl.wsb.fitnesstracker.training.api.TrainingDto;
import pl.wsb.fitnesstracker.user.api.User;

public class TrainingMaper {
    @Component
    public static class TrainingMapper {

        public TrainingDto toDto(Training training) {
            TrainingDto dto = new TrainingDto();
            dto.setId(training.getId());
            dto.setUserId(training.getUser().getId());
            dto.setStartTime(training.getStartTime());
            dto.setEndTime(training.getEndTime());
            dto.setActivityType(training.getActivityType().name());
            dto.setDistance(training.getDistance());
            dto.setAverageSpeed(training.getAverageSpeed());
            return dto;
        }

        public Training toEntity(TrainingDto dto, User user) {
            return new Training(
                    user,
                    dto.getStartTime(),
                    dto.getEndTime(),
                    ActivityType.valueOf(dto.getActivityType().toUpperCase()),
                    dto.getDistance(),
                    dto.getAverageSpeed()
            );
        }


    }
}