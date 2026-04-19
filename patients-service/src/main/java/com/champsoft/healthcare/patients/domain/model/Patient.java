package com.champsoft.healthcare.patients.domain.model;

import com.champsoft.healthcare.patients.domain.exception.PatientStatusException;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;

public class Patient {

    private final PatientId id;
    private String firstName;
    private String lastName;
    @Getter
    @Setter
    private String phoneNumber;
    @Getter
    @Setter
    private String email;
    @Getter
    @Setter
    private LocalDate dateOfBirth;
    private HealthInsuranceCard insuranceCard;
    @Getter
    private  Address address;
    @Getter
    @Setter
    private PatientStatus status;

    public Patient(PatientId id, String firstName, String lastName, String phoneNumber, String email, LocalDate dateOfBirth, HealthInsuranceCard insuranceCard, Address address, PatientStatus status) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.insuranceCard = insuranceCard;
        this.address = address;
        this.status = status;
    }

    public PatientId id(){
        return id;
    }

    public String fullName(){
        return firstName+" "+ lastName;
    }

    public String firstName(){
        return firstName;
    }

    public String lastName(){
        return lastName;
    }

    public HealthInsuranceCard insuranceCard(){
        return insuranceCard;
    }

    public PatientStatus status(){
        return status;
    }

    public void setFirstName(String firstName){
        this.firstName=firstName;
    }

    public void setLastName(String lastName){
        this.lastName=lastName;
    }

    public void updateAddress(Address a){
        this.address=a;
    }

    public void updateInsuranceCard(HealthInsuranceCard card){
        this.insuranceCard = card;
    }

    public void changeStatus(PatientStatus newStatus){
        if(newStatus==status){
            throw new PatientStatusException("Patient has already the status: " + newStatus);
        }
        this.status=newStatus;
    }

    public boolean isEligibleForAppointment(){
        return Period.between(dateOfBirth,LocalDate.now()).getYears()>=18;
    }

}
