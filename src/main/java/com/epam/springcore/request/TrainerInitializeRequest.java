package com.epam.springcore.request;


import com.epam.springcore.model.enums.TrainingType;

public class TrainerInitializeRequest {

    private String firstName;
    private String lastName;
    private TrainingType specialty;

    public TrainerInitializeRequest() {
    }

    public TrainerInitializeRequest(String ahmet, String kaya, TrainingType crossfit) {}

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
