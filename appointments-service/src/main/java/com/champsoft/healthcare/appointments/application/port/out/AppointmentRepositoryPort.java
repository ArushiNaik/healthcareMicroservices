package com.champsoft.healthcare.appointments.application.port.out;

import com.champsoft.healthcare.appointments.domain.model.Appointment;

import java.util.List;
import java.util.Optional;

public interface AppointmentRepositoryPort {
    Appointment save(Appointment appointment);
    Optional<Appointment> findById(String id);
    List<Appointment> findAll();
    void deleteById(String id);
    boolean existsById(String id);
}