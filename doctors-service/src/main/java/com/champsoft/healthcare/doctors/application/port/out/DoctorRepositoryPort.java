package com.champsoft.healthcare.doctors.application.port.out;

import com.champsoft.healthcare.doctors.domain.model.Doctor;

import java.util.List;
import java.util.Optional;

public interface DoctorRepositoryPort {

    Doctor save(Doctor doctor);

    Optional<Doctor> findById(String id);

    List<Doctor> findAll();

    void deleteById(String id);

    boolean existsById(String id);
}