package pl.wsb.fitnesstracker.training.internal;


import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import pl.wsb.fitnesstracker.training.api.TrainingDto;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/trainings")
@RequiredArgsConstructor
public class TrainingController {

        private final TrainingServiceImpl service;

        @PostMapping
        public TrainingDto create(@RequestBody TrainingDto dto) {
            return service.createTraining(dto);
        }

        @GetMapping
        public List<TrainingDto> getAll() {
            return service.getAllTrainings();
        }

        @GetMapping("/user/{userId}")
        public List<TrainingDto> getByUser(@PathVariable Long userId) {
            return service.getTrainingsByUser(userId);
        }

        @GetMapping("/before/{date}")
        public List<TrainingDto> getBefore(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date date) {
            return service.getTrainingsBefore(date);
        }

        @GetMapping("/activity/{type}")
        public List<TrainingDto> getByActivity(@PathVariable String type) {
            return service.getTrainingsByActivity(type);
        }

        @PatchMapping("/{id}/distance")
        public TrainingDto updateDistance(@PathVariable Long id, @RequestBody Map<String, Double> request) {
            return service.updateDistance(id, request.get("distance"));
        }
    }

