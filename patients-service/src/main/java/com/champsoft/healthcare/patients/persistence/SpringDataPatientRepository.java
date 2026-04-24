package com.champsoft.healthcare.patients.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataPatientRepository extends JpaRepository<PatientJpaEntity,String> {

    Optional<PatientJpaEntity> findById(String id);
    boolean existsById(String id);
    boolean existsByInsuranceCardHealthCardNum(String healthCardNum);


}
