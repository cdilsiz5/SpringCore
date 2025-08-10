package com.epam.gymcrm.model;

import com.epam.gymcrm.model.enums.Specialization;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

@Getter @Setter
@Entity
@AllArgsConstructor @NoArgsConstructor
@Table(name = "trainers")
@SuperBuilder
@ToString(onlyExplicitlyIncluded = true)
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Enumerated(EnumType.STRING)
    private Specialization specialization;

    @OneToOne(optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user;

    @ManyToMany(mappedBy = "trainers", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Trainee> trainees;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Training> trainings;
}
