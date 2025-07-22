package com.epam.springcore.model;

import com.epam.springcore.model.enums.Specialization;
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

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private Specialization name;

    @OneToMany(mappedBy = "trainingType")
    private List<Training> trainings;
}
