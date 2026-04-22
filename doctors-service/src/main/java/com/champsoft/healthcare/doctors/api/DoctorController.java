package com.champsoft.healthcare.doctors.api;

import com.champsoft.healthcare.doctors.api.dto.*;
import com.champsoft.healthcare.doctors.api.mapper.DoctorDtoMapper;
import com.champsoft.healthcare.doctors.application.service.DoctorCrudService;
import com.champsoft.healthcare.doctors.application.service.DoctorEligibilityService;
import com.champsoft.healthcare.doctors.domain.model.Doctor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorCrudService service;
    private final DoctorEligibilityService eligibilityService;

    public DoctorController(DoctorCrudService service, DoctorEligibilityService eligibilityService) {
        this.service = service;
        this.eligibilityService=eligibilityService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid CreateDoctorRequest request) {
        Doctor doctor = DoctorDtoMapper.toDomain(request);
        return ResponseEntity.ok(DoctorDtoMapper.toResponse(service.create(doctor)));
    }


    @GetMapping
    public ResponseEntity<?> getAll() {
        List<DoctorResponse> list = service.getAll()
                .stream()
                .map(DoctorDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        return ResponseEntity.ok(DoctorDtoMapper.toResponse(service.getById(String.valueOf(UUID.fromString(id)))));
    }

    @PutMapping("/{id}/info")
    public ResponseEntity<?> updateInfo(@PathVariable String id,
                                        @RequestBody @Valid UpdateDoctorRequest request) {
        Doctor updated = service.updateInfo(id, request.firstName(), request.lastName(), request.speciality());
        return ResponseEntity.ok(DoctorDtoMapper.toResponse(updated));
    }


    @PutMapping("/{id}/license")
    public ResponseEntity<?> updateLicense(@PathVariable String id,
                                           @RequestBody UpdateLicenseRequest request) {
        Doctor updated = service.updateLicense(String.valueOf(UUID.fromString(id)), request.getLicenseExpiryDate());
        return ResponseEntity.ok(DoctorDtoMapper.toResponse(updated));
    }


    @PutMapping("/{id}/activate")
    public ResponseEntity<?> activate(@PathVariable String id) {
        Doctor updated = service.activate(String.valueOf(UUID.fromString(id)));
        return ResponseEntity.ok(DoctorDtoMapper.toResponse(updated));
    }


    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable String id) {
        Doctor updated = service.deactivate(String.valueOf(UUID.fromString(id)));
        return ResponseEntity.ok(DoctorDtoMapper.toResponse(updated));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        service.delete(UUID.fromString(id));
        return ResponseEntity.ok("Doctor deleted");
    }
    @GetMapping("/{id}/eligibility")
    public ResponseEntity<Boolean> isEligible(@PathVariable String id){
        return ResponseEntity.ok(eligibilityService.isEligible(id));
    }
}