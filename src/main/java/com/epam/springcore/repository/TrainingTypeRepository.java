package com.epam.springcore.repository;

import com.epam.springcore.model.TrainingType;
import com.epam.springcore.model.enums.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingTypeRepository  extends JpaRepository<TrainingType,Long> {
    boolean existsByName(Specialization name);

}
