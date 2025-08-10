package com.epam.gymcrm.repository;

import com.epam.gymcrm.model.Trainee;
import com.epam.gymcrm.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TrainingRepository  extends JpaRepository<Training,Long> {

    List<Training> findByTraineeAndTrainerIsNotNull(Trainee trainee);
    @Query("""
        SELECT t
        FROM Training t
          JOIN FETCH t.trainer tr
          JOIN FETCH tr.user uTr
          JOIN FETCH t.trainee tn
          JOIN FETCH tn.user uTn
        WHERE uTn.username = :username
          AND (:from IS NULL OR t.date >= :from)
          AND (:to IS NULL OR t.date <= :to)
          AND (:trainerName IS NULL OR LOWER(uTr.firstName) LIKE LOWER(CONCAT('%', :trainerName, '%')))
          AND (:trainerLastName IS NULL OR LOWER(uTr.lastName) LIKE LOWER(CONCAT('%', :trainerLastName, '%')))
        ORDER BY t.date DESC, t.id DESC
        """)
    List<Training> findHistoryForTrainer(
            @Param("username") String username,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("trainerName") String trainerName,
            @Param("trainerLastName") String trainerLastName
    );

}

