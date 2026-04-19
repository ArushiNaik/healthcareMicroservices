package com.champsoft.healthcare.appointments.infrastructure.persistance;

import com.champsoft.healthcare.appointments.infrastructure.persistance.AppointmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataAppointmentRepository
        extends JpaRepository<AppointmentJpaEntity, String> {}