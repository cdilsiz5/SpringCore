package com.epam.springcore.repository;


import com.epam.springcore.model.Trainee;
import com.epam.springcore.model.Trainer;
import com.epam.springcore.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TrainingRepository  extends JpaRepository<Trainee,Long> {

    List<Training> findAllByTrainer(Trainer trainers);
    List<Training> findAllByTrainee(Trainee trainee);
}
