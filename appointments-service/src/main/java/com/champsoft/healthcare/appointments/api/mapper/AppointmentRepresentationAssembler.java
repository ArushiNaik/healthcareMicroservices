package com.champsoft.healthcare.appointments.api.mapper;

import com.champsoft.healthcare.appointments.api.AppointmentController;
import com.champsoft.healthcare.appointments.api.dto.AppointmentResponse;
import com.champsoft.healthcare.appointments.domain.model.Appointment;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class AppointmentRepresentationAssembler {

    public EntityModel<AppointmentResponse> toModel(Appointment entity) {

        AppointmentResponse response =
                AppointmentRepresentationMapper.toResponse(entity);

        String id = entity.id().value();

        return EntityModel.of(response,
                linkTo(methodOn(AppointmentController.class)
                        .getById(id))
                        .withSelfRel(),

                linkTo(methodOn(AppointmentController.class)
                        .getAll())
                        .withRel("all-appointments"),

                linkTo(methodOn(AppointmentController.class)
                        .delete(id))
                        .withRel("delete"),

                Link.of("/api/appointments/" + id + "/reschedule")
                        .withRel("reschedule"));
//                ,
//
//                linkTo(methodOn(AppointmentController.class)
//                        .complete(id))
//                        .withRel("complete")
//        );
    }

    public CollectionModel<EntityModel<AppointmentResponse>> toCollection(
            List<Appointment> appointments
    ) {
        var models = appointments.stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(models,
                linkTo(methodOn(AppointmentController.class).getAll()).withSelfRel()
        );
    }

    public EntityModel<Void> deletionResponse(String id) {
        return EntityModel.of(null,
                linkTo(methodOn(AppointmentController.class)
                        .getAll())
                        .withRel("all-appointments")
        );
    }
}