package com.epam.springcore.repository;


import com.epam.springcore.model.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainerRepository extends JpaRepository<Trainee, Long> {

}
