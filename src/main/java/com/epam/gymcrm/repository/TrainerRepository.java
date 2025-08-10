package com.epam.gymcrm.repository;


import com.epam.gymcrm.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    Optional<Trainer> findByUserUsername(String username);

        @Query("""
        select tr
        from Trainer tr
        where not exists (
            select 1
            from Trainee tn
                join tn.trainers trx
            where tn.user.username = :username
              and trx.id = tr.id
        )
        order by tr.id desc
        """)
        List<Trainer> findUnassignedForTraineeUsername(@Param("username") String username);
    }


}
