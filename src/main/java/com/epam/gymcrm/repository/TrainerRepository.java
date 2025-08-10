package com.epam.gymcrm.repository;


import com.epam.gymcrm.model.Trainer;
import com.epam.gymcrm.model.enums.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    boolean existsBySpecialization(Specialization specialization);

    Optional<Trainer> findByUserUsername(String username);
    Optional<Trainer> findByUser_Username(String username);
    @Query("""
        SELECT tr
        FROM Trainer tr
        WHERE NOT EXISTS (
            SELECT 1
            FROM Training t
            WHERE t.trainer = tr
              AND t.trainee.user.username = :username
        )
        ORDER BY tr.id ASC
        """)
    List<Trainer> findUnassignedForTraineeUsername(@Param("username") String username);
}
