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
    select t from Training t
      join t.trainer tr
      join tr.user trainerUser
      join t.trainee ta
      join ta.user traineeUser
    where trainerUser.username = :username
      and t.date >= coalesce(:from, t.date)
      and t.date <= coalesce(:to,   t.date)
      and (coalesce(:traineeFirstName, '') = '' or lower(traineeUser.firstName) like lower(concat('%', :traineeFirstName, '%')))
      and (coalesce(:traineeLastName,  '') = '' or lower(traineeUser.lastName)  like lower(concat('%', :traineeLastName,  '%')))
    order by t.date desc, t.id desc
    """)
    List<Training> findHistoryForTrainer(
            @Param("username") String username,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("traineeFirstName") String traineeFirstName,
            @Param("traineeLastName") String traineeLastName
    );

    @Query("""
    select t from Training t
      join t.trainer tr
      join tr.user trainerUser
      join t.trainee ta
      join ta.user traineeUser
    where traineeUser.username = :username
      and t.date >= coalesce(:from, t.date)
      and t.date <= coalesce(:to,   t.date)
      and (coalesce(:trainerFirstName, '') = '' or lower(trainerUser.firstName) like lower(concat('%', :trainerFirstName, '%')))
      and (coalesce(:trainerLastName,  '') = '' or lower(trainerUser.lastName)  like lower(concat('%', :trainerLastName,  '%')))
    order by t.date desc, t.id desc
    """)
    List<Training> findHistoryForTrainee(
            @Param("username") String username,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("trainerFirstName") String trainerFirstName,
            @Param("trainerLastName") String trainerLastName
    );
}



