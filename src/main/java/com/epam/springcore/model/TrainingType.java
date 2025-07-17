package com.epam.springcore.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "training_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "trainingType")
    private List<Training> trainings;
}
