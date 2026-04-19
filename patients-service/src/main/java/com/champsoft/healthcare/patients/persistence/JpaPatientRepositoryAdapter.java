package com.champsoft.healthcare.patients.persistence;

import com.champsoft.healthcare.patients.application.port.out.PatientRepositoryPort;
import com.champsoft.healthcare.patients.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JpaPatientRepositoryAdapter implements PatientRepositoryPort {

    private final SpringDataPatientRepository jpa;

    public JpaPatientRepositoryAdapter(SpringDataPatientRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Patient save(Patient patient) {
        var e = toEntity(patient);
        jpa.save(e);
        return patient;
    }

    @Override
    public Optional<Patient> findById(PatientId patientId) {
        return jpa.findById(patientId.getId()).map(this::toDomain);
    }

    @Override
    public List<Patient> findAll() {
        return jpa.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteById(PatientId patientId) {
        jpa.deleteById(patientId.getId());
    }

    @Override
    public boolean existsById(PatientId patientId) {
        return jpa.existsById(patientId.getId());
    }

    @Override
    public boolean existByInsuranceCard(String cardNumber) {
        return false;
    }

    private PatientJpaEntity toEntity(Patient p){
        var a = new PatientJpaEntity();
        a.id = p.id().getId();
        a.firstName = p.firstName();
        a.lastName=p.lastName();
        a.phoneNumber=p.getPhoneNumber();
        a.email=p.getEmail();
        a.dateOfBirth=p.getDateOfBirth();
        a.insuranceCard=p.insuranceCard();
        a.address=p.getAddress();
//        a.status= String.valueOf(p.getStatus());
        a.status= p.status().name();

        return a;
    }

    public Patient toDomain(PatientJpaEntity e){
        var d = new Patient(
                PatientId.of(e.id),e.firstName,e.lastName,e.phoneNumber,e.email,e.dateOfBirth,
                new HealthInsuranceCard(e.insuranceCard.insuranceCardNumber(),e.insuranceCard.getExpiryDate()),
                new Address(e.address.getStreetNumber(), e.address.getStreetName(),e.address.getCity(),e.address.getPostalCode(), e.address.getCountry()),
                PatientStatus.valueOf(e.status)
                );
            return d;
    }
}
