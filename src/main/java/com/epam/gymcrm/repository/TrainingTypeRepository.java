package com.epam.gymcrm.repository;

import com.epam.gymcrm.model.TrainingType;
import com.epam.gymcrm.model.enums.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainingTypeRepository  extends JpaRepository<TrainingType,Long> {
    boolean existsByName(Specialization name);
    Optional<TrainingType> findByName(Specialization name);

}
