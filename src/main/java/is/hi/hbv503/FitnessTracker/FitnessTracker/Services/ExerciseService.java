package is.hi.hbv503.FitnessTracker.FitnessTracker.Services;

import is.hi.hbv503.FitnessTracker.FitnessTracker.Entities.Exercise;

import java.util.List;
import java.util.Optional;
import java.util.Date;

public interface ExerciseService {
    Exercise save(Exercise exercise);
    void delete(Exercise exercise);
    List<Exercise> findAll();
}
