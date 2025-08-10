package com.epam.gymcrm.repository;
import com.epam.gymcrm.model.Trainee;
import com.epam.gymcrm.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TraineeRepository  extends JpaRepository<Trainee, Long> {
    Optional<Trainee> findByUserUsername(String username);

    @Query("""
      select distinct t
      from Training t
        join fetch t.trainee tn
        join fetch tn.user uTrainee
        join fetch t.trainer tr
        join fetch tr.user uTrainer
        join fetch t.trainingType tt
      where uTrainee.username = :username
        and (:from is null or t.date >= :from)
        and (:to   is null or t.date <= :to)
        and (:trainerName     is null or lower(uTrainer.firstName) like lower(concat('%', :trainerName, '%')))
        and (:trainerLastName is null or lower(uTrainer.lastName)  like lower(concat('%', :trainerLastName, '%')))
      order by t.date desc, t.id desc
      """)
    List<Training> findHistoryForTraineeWithDetails(
            @Param("username") String username,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("trainerName") String trainerName,
            @Param("trainerLastName") String trainerLastName
    );

}
