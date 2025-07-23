package com.epam.springcore.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "trainings")
@SuperBuilder
 public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long traineeId;
    private Long trainerId;
    private LocalDate date;
    private Integer durationMinutes;

   @ManyToOne
   @JoinColumn(name = "trainer_id")
   private Trainer trainer;

   @ManyToOne
   @JoinColumn(name = "trainee_id")
   private Trainee trainee;

   @ManyToOne
   @JoinColumn(name = "training_type_id")
   private TrainingType trainingType;
}

