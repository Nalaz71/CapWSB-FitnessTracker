package pl.wsb.fitnesstracker.training.api;

import lombok.*;
import pl.wsb.fitnesstracker.training.internal.ActivityType;
import pl.wsb.fitnesstracker.user.api.User;

import java.util.Date;

/**
 * Data Transfer Object for Training.
 * This class is used to transfer training data between layers.
 */
@Getter
@Setter
@ToString
@Data
@AllArgsConstructor
public class TrainingDto {

    private User user;
    private Date startTime;
    private Date endTime;
    private ActivityType activityType;
    private double distance;
    private double averageSpeed;

}