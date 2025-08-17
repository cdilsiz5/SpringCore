package com.epam.gymcrm.repository;

import com.epam.gymcrm.model.Trainee;
import com.epam.gymcrm.model.Training;
import com.epam.gymcrm.model.enums.Specialization;
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
      join t.trainingType type
    where trainerUser.username = :username
      and t.date >= coalesce(:from, t.date)
      and t.date <= coalesce(:to,   t.date)
      and (coalesce(:traineeName, '') = '' or lower(traineeUser.firstName) like lower(concat('%', :traineeName, '%')))
      and (coalesce(:trainingType, '') = '' or lower(type.name) like lower(concat('%', :trainingType, '%')))
    order by t.date desc, t.id desc
""")
    List<Training> findHistoryForTrainer(
            @Param("username") String username,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("traineeName") String traineeName,
            @Param("trainingType") String trainingType
    );


    @Query("""
select t from Training t
 join t.trainer tr
 join tr.user trainerUser
 join t.trainee ta
 join ta.user traineeUser
where traineeUser.username = :username
  and t.date >= coalesce(:from, t.date)
  and t.date <= coalesce(:to, t.date)
  and (:trainerName is null or
       lower(concat(trainerUser.firstName, ' ', trainerUser.lastName)) like lower(concat('%', :trainerName, '%')))
  and (:trainingType is null or t.trainingType.name = :trainingType)
order by t.date desc
""")
    List<Training> findHistoryForTrainee(
            @Param("username") String username,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("trainerName") String trainerName,
            @Param("trainingType") Specialization trainingType
    );

}



