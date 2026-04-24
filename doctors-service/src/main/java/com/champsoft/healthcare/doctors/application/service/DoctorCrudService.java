package com.champsoft.healthcare.doctors.application.service;

import com.champsoft.healthcare.doctors.application.exception.DoctorNotFoundException;
import com.champsoft.healthcare.doctors.application.port.out.DoctorRepositoryPort;
import com.champsoft.healthcare.doctors.domain.exception.DoctorLicenseExpiredException;
import com.champsoft.healthcare.doctors.domain.exception.DuplicateDoctorException;
import com.champsoft.healthcare.doctors.domain.model.Doctor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class DoctorCrudService {

    private final DoctorRepositoryPort repository;

    public DoctorCrudService(DoctorRepositoryPort repository) {
        this.repository = repository;
    }

    @Transactional
    public Doctor create(Doctor doctor) {
        if(repository.existsById(doctor.getId()))throw new DuplicateDoctorException("Doctor Id already exist");
        return repository.save(doctor);
    }

    @Transactional(readOnly = true)
    public List<Doctor> getAll() {
        return repository.findAll();
    }

//    @Transactional(readOnly = true)
//    public Doctor getById(String id) {
//        return repository.findById(String.valueOf(id))
//                .orElseThrow(() -> new RuntimeException("Doctor not found: " + id));
//    }


    @Transactional

    public Doctor updateInfo(String id, String firstName, String lastName, String specialty) {
        Doctor doctor = getById(String.valueOf(UUID.fromString(id)));
        doctor.updateInfo(firstName, lastName, specialty);
        return repository.save(doctor);
    }


//    @Transactional
//    public Doctor updateLicense(String id, LocalDate expiryDate) {
//
//        Doctor doctor = getById(String.valueOf(id));
//
//        if (expiryDate == null) {
//            throw new RuntimeException("License expiry cannot be null");
//        }
//
//        doctor.updateLicense(expiryDate);
//
//        return repository.save(doctor);
//    }


    @Transactional
    public Doctor activate(String id) {
        Doctor doctor = getById(id);
        doctor.activate();
        return repository.save(doctor);
    }


    @Transactional
    public Doctor deactivate(String id) {
        Doctor doctor = getById(id);
        doctor.deactivate();
        return repository.save(doctor);
    }

//    @Transactional
//    public void delete(UUID id) {
//        if (!repository.existsById(String.valueOf(id))) {
//            throw new RuntimeException("Doctor not found: " + id);
//        }
//        repository.deleteById(String.valueOf(id));
//    }

    @Transactional(readOnly = true)
    public Doctor getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException("Doctor not found: " + id));
    }

    @Transactional
    public Doctor updateLicense(String id, LocalDate expiryDate) {
        Doctor doctor = getById(id);

        if (expiryDate == null) {
            throw new DoctorLicenseExpiredException("License expiry cannot be null");
        }

        doctor.updateLicense(expiryDate);
        return repository.save(doctor);
    }

    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(String.valueOf(id))) {
            throw new DoctorNotFoundException("Doctor not found: " + id);
        }
        repository.deleteById(String.valueOf(id));
    }
}
