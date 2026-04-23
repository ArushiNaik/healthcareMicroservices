package com.champsoft.healthcare.appointments.api;

import com.champsoft.healthcare.appointments.api.dto.*;
import com.champsoft.healthcare.appointments.api.mapper.AppointmentRepresentationAssembler;
import com.champsoft.healthcare.appointments.application.service.AppointmentOrchestrator;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.web.bind.annotation.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;


@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentOrchestrator orchestrator;
    private final AppointmentRepresentationAssembler assembler;

    public AppointmentController(AppointmentOrchestrator orchestrator,
                                 AppointmentRepresentationAssembler assembler) {
        this.orchestrator = orchestrator;
        this.assembler = assembler;
    }

    @PostMapping
    public EntityModel<AppointmentResponse> create(@RequestBody CreateAppointmentRequest req) {
        var appointment = orchestrator.create(
                req.doctorId, req.patientId, req.billingId, req.time
        );
        return assembler.toModel(appointment);
    }

    @GetMapping("/{id}")
    public EntityModel<AppointmentResponse> getById(@PathVariable String id) {
        return assembler.toModel(orchestrator.getById(id));
    }

    @GetMapping
    public CollectionModel<EntityModel<AppointmentResponse>> getAll() {
        return assembler.toCollection(orchestrator.getAll());
    }

    @DeleteMapping("/{id}")
    public EntityModel<Void> delete(@PathVariable String id) {
        orchestrator.delete(id);
        return assembler.deletionResponse(id);
    }

    @PutMapping("/{id}/reschedule")
    public EntityModel<AppointmentResponse> reschedule(
            @PathVariable String id,
            @RequestBody RescheduleAppointmentRequest req
    ) {
        return assembler.toModel(
                orchestrator.reschedule(id, req.newTime)
        );
    }

    @PutMapping("/{id}/complete")
    public EntityModel<AppointmentResponse> complete(@PathVariable String id) {
        return assembler.toModel(orchestrator.complete(id));
    }
}
