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

    public AppointmentController(AppointmentOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping
    public EntityModel<AppointmentResponse> create(@RequestBody CreateAppointmentRequest req) {
        return AppointmentRepresentationAssembler.toModel(
                orchestrator.create(req.doctorId, req.patientId, req.billingId, req.time)
        );
    }

    @GetMapping
    public CollectionModel<EntityModel<AppointmentResponse>> getAll() {

        var list = orchestrator.getAll().stream()
                .map(AppointmentRepresentationAssembler::toModel)
                .toList();

        return CollectionModel.of(list);
    }

    @GetMapping("/{id}")
    public EntityModel<AppointmentResponse> getById(@PathVariable String id) {
        return AppointmentRepresentationAssembler.toModel(
                orchestrator.getById(id)
        );
    }

    @DeleteMapping("/{id}")
    public EntityModel<Void> delete(@PathVariable String id) {
        orchestrator.delete(id);

        return EntityModel.of(null,
                linkTo(methodOn(AppointmentController.class).getAll()).withRel("all-appointments")
        );
    }

    @PutMapping("/{id}/reschedule")
    public EntityModel<AppointmentResponse> reschedule(
            @PathVariable String id,
            @RequestBody RescheduleAppointmentRequest req) {

        return AppointmentRepresentationAssembler.toModel(
                orchestrator.reschedule(id, req.newTime)
        );
    }

    @PutMapping("/{id}")
    public EntityModel<AppointmentResponse> update(
            @PathVariable String id,
            @RequestBody UpdateAppointmentRequest req) {

        return AppointmentRepresentationAssembler.toModel(
                orchestrator.update(id, req)
        );
    }

    @PutMapping("/{id}/complete")
    public EntityModel<AppointmentResponse> complete(@PathVariable String id) {
        return AppointmentRepresentationAssembler.toModel(
                orchestrator.complete(id)
        );
    }
}