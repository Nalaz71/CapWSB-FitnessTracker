package pl.wsb.fitnesstracker.training.api;

import lombok.Data;
import pl.wsb.fitnesstracker.training.internal.ActivityType;

import java.time.LocalDateTime;

@Data
public class TrainingRequestDto {

    private Long userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ActivityType activityType;
    private double distance;
    private double averageSpeed;

}
