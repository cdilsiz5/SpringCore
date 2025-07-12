package com.epam.springcore.request;

import com.epam.springcore.model.enums.TrainingType;

import jakarta.validation.constraints.*;
public class CreateTrainerRequest {


    @NotBlank(message = "First name cannot be blank")
    @Size(max = 50, message = "First name must be at most 50 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 50, message = "Last name must be at most 50 characters")
    private String lastName;

    @NotNull(message = "Specialty must be specified")
    private TrainingType specialty;

    public CreateTrainerRequest(String firstName, String lastName, TrainingType specialty) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialty = specialty;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public TrainingType getSpecialty() {
        return specialty;
    }

    public void setSpecialty(TrainingType specialty) {
        this.specialty = specialty;
    }
}
