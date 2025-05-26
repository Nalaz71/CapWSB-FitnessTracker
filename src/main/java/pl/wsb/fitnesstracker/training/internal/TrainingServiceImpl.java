package pl.wsb.fitnesstracker.training.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.wsb.fitnesstracker.training.api.Training;
import pl.wsb.fitnesstracker.training.api.TrainingDto;
import pl.wsb.fitnesstracker.training.api.TrainingProvider;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.api.UserService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

// TODO: Provide Implementation and correct the return type of the method getTraining
@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingProvider {

    private final TrainingRepository trainingRepository;
    private final UserService userService;
    private final TrainingMaper.TrainingMapper trainingMapper;

    public TrainingDto createTraining(TrainingDto dto) {
        User user = userService.getUser(dto.getUserId()).orElseThrow();
        Training training = trainingMapper.toEntity(dto, user);
        return trainingMapper.toDto(trainingRepository.save(training));
    }

    public List<TrainingDto> getAllTrainings() {
        return trainingRepository.findAll()
                .stream()
                .map(trainingMapper::toDto)
                .toList();
    }

    public List<TrainingDto> getTrainingsByUser(Long userId) {
        return trainingRepository.findByUserId(userId)
                .stream()
                .map(trainingMapper::toDto)
                .toList();
    }

    public List<TrainingDto> getTrainingsBefore(Date date) {
        return trainingRepository.findByEndTimeBefore(date)
                .stream()
                .map(trainingMapper::toDto)
                .toList();
    }

    public List<TrainingDto> getTrainingsByActivity(String activity) {
        ActivityType type = ActivityType.valueOf(activity.toUpperCase());
        return trainingRepository.findByActivityType(type)
                .stream()
                .map(trainingMapper::toDto)
                .toList();
    }

    public TrainingDto updateDistance(Long id, double distance) {
        Training training = trainingRepository.findById(id).orElseThrow();
        training.setDistance(distance);
        return trainingMapper.toDto(trainingRepository.save(training));
    }
    @Override
    public Optional<User> getTraining(final Long trainingId) {
        throw new UnsupportedOperationException("Not finished yet");
    }


}



