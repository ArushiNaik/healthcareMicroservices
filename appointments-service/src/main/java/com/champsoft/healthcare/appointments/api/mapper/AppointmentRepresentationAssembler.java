package com.champsoft.healthcare.appointments.api.mapper;

import com.champsoft.healthcare.appointments.api.dto.AppointmentResponse;
import com.champsoft.healthcare.appointments.domain.model.Appointment;
import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.champsoft.healthcare.appointments.api.AppointmentController;

public class AppointmentRepresentationAssembler {

    public static EntityModel<AppointmentResponse> toModel(Appointment a) {

        AppointmentResponse r = new AppointmentResponse();

        r.id = a.id().value();
        r.doctorId = a.doctorId().value();
        r.patientId = a.patientId().value();
        r.billingId = a.getBillingRef().value();
        r.status = a.status().name();
        r.time = a.time().value();

        return EntityModel.of(r,
                linkTo(methodOn(AppointmentController.class)
                        .getById(r.id)).withSelfRel(),

                linkTo(methodOn(AppointmentController.class)
                        .getAll()).withRel("all-appointments"),

                linkTo(methodOn(AppointmentController.class)
                        .delete(r.id)).withRel("delete"),

                linkTo(methodOn(AppointmentController.class)
                        .reschedule(r.id, null)).withRel("reschedule"),

                linkTo(methodOn(AppointmentController.class)
                        .complete(r.id)).withRel("complete")
        );
    }
}