package com.champsoft.healthcare.patients.api;

import com.champsoft.healthcare.patients.api.dto.*;
import com.champsoft.healthcare.patients.api.mapper.PatientApiMapper;
import com.champsoft.healthcare.patients.application.service.PatientCrudService;
import com.champsoft.healthcare.patients.domain.model.*;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientCrudService service;

    public PatientController(PatientCrudService service){
        this.service=service;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid CreatePatientRequest req){
        HealthInsuranceCard card= new HealthInsuranceCard(req.healthCardNum(),req.expiryDate());
        Address address = new Address(req.streetNumber(),req.streetName(),req.city(),req.postalCode(),req.Country());
        var v = service.create(req.firstName(),req.lastName(), req.phoneNumber(),req.email(),req.dateOfBirth(),card,address, PatientStatus.STABLE);
        return ResponseEntity.ok(PatientApiMapper.toResponse(v));
    }

    //get by id
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable String id){
        return ResponseEntity.ok(PatientApiMapper.toResponse(service.findById(id)));
    }
    //get all +
    @GetMapping
    public ResponseEntity<?> list(){
        List<?> patients = service.list().stream().map(PatientApiMapper::toResponse).toList();
        return ResponseEntity.ok(patients);
    }
    //update patient card number
    @PutMapping("/{id}/insuranceCard")
    public ResponseEntity<?> updatePatientCard(@PathVariable String id, @RequestBody @Valid UpdatePatientRequest req){
        var v = service.updatePatientCard(id,req.healthCardNum(),req.expiryDate());
        return ResponseEntity.ok(PatientApiMapper.toResponseHealthCard(v));
    }
    //update address
    @PutMapping("/{id}/address")
    public ResponseEntity<?> updateAddress(@PathVariable String id, @RequestBody @Valid UpdateAddressRequest req){
        var v = service.updateAddress(id,req.streetNumber(), req.streetName(), req.city(),req.postalCode(), req.Country());
        return ResponseEntity.ok(PatientApiMapper.toResponseAddress(v));
    }
    //delete
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id){
        service.delete(id);
    }

    @PostMapping("/{id}/status/{status}")
    public ResponseEntity<?> status(@PathVariable String id, @PathVariable PatientStatus status){
        var patient = service.changeStatus(id,status);
        return ResponseEntity.ok(PatientApiMapper.toResponse(patient));
    }
}
