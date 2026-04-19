package com.champsoft.healthcare.doctors.persistence;

import com.champsoft.healthcare.doctors.application.port.out.DoctorRepositoryPort;
import com.champsoft.healthcare.doctors.domain.model.Doctor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JpaDoctorRepositoryAdapter implements DoctorRepositoryPort {

    private final DoctorRepository repository;

    public JpaDoctorRepositoryAdapter(DoctorRepository repository) {
        this.repository = repository;
    }

    @Override
    public Doctor save(Doctor doctor) {

        DoctorJpaEntity entity = repository.findById(String.valueOf(UUID.fromString(doctor.getId())))
                .orElse(new DoctorJpaEntity());

        entity.setId(String.valueOf(UUID.fromString(doctor.getId())));
        entity.setFirstName(doctor.getFirstName());
        entity.setLastName(doctor.getLastName());
        entity.setSpecialty(doctor.getSpecialty());
        entity.setLicenseExpiryDate(doctor.getLicenseExpiryDate());
        entity.setActive(doctor.isActive());

        return DoctorMapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Doctor> findById(String id) {
        return repository.findById(String.valueOf(UUID.fromString(id))).map(DoctorMapper::toDomain);
    }

    @Override
    public List<Doctor> findAll() {
        return repository.findAll()
                .stream()
                .map(DoctorMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(String.valueOf(UUID.fromString(id)));
    }

    @Override
    public boolean existsById(String id) {
        return repository.existsById(String.valueOf(UUID.fromString(id)));
    }
}

