package com.epam.gymcrm.repository;

import com.epam.gymcrm.model.Trainee;
import com.epam.gymcrm.model.Trainer;
import com.epam.gymcrm.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TrainingRepository  extends JpaRepository<Training,Long> {

    List<Training> findAllByTrainer(Trainer trainers);
    List<Training> findAllByTrainee(Trainee trainee);
    @Query("""
        select distinct t
        from Training t
          join fetch t.trainer tr
          join fetch tr.user uTrainer
          join fetch t.trainee tn
          join fetch tn.user uTrainee
          join fetch t.trainingType tt
        where uTrainer.username = :username
          and (:from is null or t.date >= :from)
          and (:to   is null or t.date <= :to)
          and (:traineeName     is null or lower(uTrainee.firstName) like lower(concat('%', :traineeName, '%')))
          and (:traineeLastName is null or lower(uTrainee.lastName)  like lower(concat('%', :traineeLastName, '%')))
        order by t.date desc, t.id desc
        """)
    List<Training> findHistoryForTrainer(
            @Param("username") String username,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("traineeName") String traineeName,
            @Param("traineeLastName") String traineeLastName
    );

}
