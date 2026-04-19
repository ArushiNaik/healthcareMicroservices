package com.champsoft.healthcare.patients.persistence;

import com.champsoft.healthcare.patients.domain.model.Address;
import com.champsoft.healthcare.patients.domain.model.HealthInsuranceCard;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name="patient")
public class PatientJpaEntity {

    @Id
    public String id;

    @Column(nullable = false)
    public String firstName;

    @Column(nullable = false)
    public String lastName;

    @Column(nullable = false,unique = true)
    public String phoneNumber;

    @Column(nullable = false,unique = true)
    public String email;

    @Column(nullable = false)
    public LocalDate dateOfBirth;

    @Embedded
    public HealthInsuranceCard insuranceCard;

    @Embedded
    public Address address;

    @Column(nullable = false)
    public String status;


}
