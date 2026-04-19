package com.champsoft.healthcare.appointments.api;

import com.champsoft.healthcare.appointments.api.dto.*;
import com.champsoft.healthcare.appointments.api.mapper.AppointmentRepresentationAssembler;
import com.champsoft.healthcare.appointments.application.service.AppointmentOrchestrator;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentOrchestrator orchestrator;

    public AppointmentController(AppointmentOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping
    public AppointmentResponse create(@RequestBody CreateAppointmentRequest req) {
        return AppointmentRepresentationAssembler.toResponse(
                orchestrator.create(req.doctorId, req.patientId, req.billingId,req.time)
        );
    }

    @GetMapping
    public List<AppointmentResponse> getAll() {
        return orchestrator.getAll().stream()
                .map(AppointmentRepresentationAssembler::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public AppointmentResponse getById(@PathVariable String id) {
        return AppointmentRepresentationAssembler.toResponse(orchestrator.getById(id));
    }



    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        orchestrator.delete(id);
    }


    @PutMapping("/{id}/reschedule")
    public AppointmentResponse reschedule(@PathVariable String id,
                                          @RequestBody RescheduleAppointmentRequest req) {

        return AppointmentRepresentationAssembler.toResponse(
                orchestrator.reschedule(id, req.newTime)
        );
    }
    @PutMapping("/{id}")
    public AppointmentResponse update(@PathVariable String id,
                                      @RequestBody UpdateAppointmentRequest req) {

        return AppointmentRepresentationAssembler.toResponse(
                orchestrator.update(id, req)
        );
    }
    @PutMapping("/{id}/complete")
    public AppointmentResponse complete(@PathVariable String id) {
        return AppointmentRepresentationAssembler.toResponse(
                orchestrator.complete(id)
        );
    }
}