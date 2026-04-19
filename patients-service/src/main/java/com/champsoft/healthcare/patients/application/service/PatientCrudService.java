package com.champsoft.healthcare.patients.application.service;

import com.champsoft.healthcare.patients.application.exception.DuplicatePatientException;
import com.champsoft.healthcare.patients.application.port.out.PatientRepositoryPort;
import com.champsoft.healthcare.patients.domain.exception.PatientNotFoundException;
import com.champsoft.healthcare.patients.domain.model.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PatientCrudService {

    private final PatientRepositoryPort repo;

    public PatientCrudService(@Qualifier("jpaPatientRepositoryAdapter") PatientRepositoryPort repo){
        this.repo =repo;
    }

    @Transactional
    public Patient create(String firstName, String lastName, String phoneNumber, String email, LocalDate dateOfBirth, HealthInsuranceCard insuranceCard, Address address, PatientStatus status){
        HealthInsuranceCard h = new HealthInsuranceCard(insuranceCard.insuranceCardNumber(),insuranceCard.getExpiryDate());
        if(repo.existByInsuranceCard(h.insuranceCardNumber())) throw new DuplicatePatientException("Patient already exists");
        Address address1 = new Address(address.getStreetNumber(),address.getStreetName(), address.getCity(),address.getPostalCode(), address.getCountry());
        var patient = new Patient(PatientId.newId(),firstName,lastName,phoneNumber,email,dateOfBirth,h,address1,status);

        return repo.save(patient);
    }

    @Transactional(readOnly = true)
    public Patient findById(String id){
        return repo.findById(new PatientId(id)).orElseThrow(()-> new PatientNotFoundException("Patient: " + id+" not found"));
    }

    @Transactional(readOnly = true)
    public List<Patient> list(){
        return repo.findAll();
    }

    //update
    @Transactional
    public Patient updatePatientCard(String id,String newCardNum, LocalDate newExpiryDate){
        var patient = findById(id);
        patient.updateInsuranceCard(new HealthInsuranceCard(newCardNum,newExpiryDate));
        return repo.save(patient);
    }

    @Transactional
    public Patient updateAddress(String id, Integer streetNumber,String streetName, String city, String postalCode, String Country){
        var patient = findById(id);
        patient.updateAddress(new Address(streetNumber,streetName,city,postalCode,Country));
        return repo.save(patient);
    }
    @Transactional
    public void delete(String id){
        findById(id);
        repo.deleteById(PatientId.of(id));
    }

    @Transactional
    public Patient changeStatus(String id,PatientStatus status){
        var patient = findById(id);
        patient.changeStatus(status);
        return repo.save(patient);

    }


}
