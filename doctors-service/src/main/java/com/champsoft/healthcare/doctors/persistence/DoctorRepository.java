package com.champsoft.healthcare.doctors.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<DoctorJpaEntity, String> {
}