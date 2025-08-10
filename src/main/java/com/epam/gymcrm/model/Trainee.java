package com.epam.gymcrm.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter @Setter
@Entity
@AllArgsConstructor @NoArgsConstructor
@Table(name = "trainees")
@SuperBuilder
@ToString(onlyExplicitlyIncluded = true)
public class Trainee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    private LocalDate dateOfBirth;
    private String address;

    @OneToOne(optional = false, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "trainee_trainer",
            joinColumns = @JoinColumn(name = "trainee_id"),
            inverseJoinColumns = @JoinColumn(name = "trainer_id"))
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Trainer> trainers;

    @OneToMany(mappedBy = "trainee", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Training> trainings;
}
