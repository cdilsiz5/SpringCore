package com.epam.springcore.request;

import java.time.LocalDate;

public class TraineeInitializeRequest {

    private String id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String address;

    public TraineeInitializeRequest() {
    }

    public TraineeInitializeRequest(String ali, String yılmaz, String date, String ankara) {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
