package com.champsoft.healthcare.appointments.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

// Domain test → pure business rule testing
// NO Spring, NO Mockito, NO database
class AppointmentTest {

    // A future appointment time (always valid - 30 days from now)
    private static final LocalDateTime FUTURE = LocalDateTime.now().plusDays(30);

    // Helper → creates a valid Appointment
    private Appointment validAppointment(String id) {
        return new Appointment(
                new AppointmentId(id),
                new DoctorRef("doctor-1"),
                new PatientRef("patient-1"),
                new BillingRef("billing-1"),
                new AppointmentTime(FUTURE)
        );
    }

    @Test
    void shouldCreateAppointmentWithScheduledStatus() {

        // ------------------- Act -------------------
        Appointment appt = validAppointment("appt-1");

        // ------------------- Assert -------------------
        // Business rule: new appointment always starts SCHEDULED.
        assertThat(appt.id().value()).isEqualTo("appt-1");
        assertThat(appt.doctorId().value()).isEqualTo("doctor-1");
        assertThat(appt.patientId().value()).isEqualTo("patient-1");
        assertThat(appt.getBillingRef().value()).isEqualTo("billing-1");
        assertThat(appt.status()).isEqualTo(AppointmentStatus.SCHEDULED);
    }

    @Test
    void shouldCancelAppointmentSuccessfully() {

        // ------------------- Arrange -------------------
        Appointment appt = validAppointment("appt-1");
        assertThat(appt.status()).isEqualTo(AppointmentStatus.SCHEDULED);

        // ------------------- Act -------------------
        appt.cancel();

        // ------------------- Assert -------------------
        assertThat(appt.status()).isEqualTo(AppointmentStatus.CANCELLED);
    }

    @Test
    void shouldRescheduleScheduledAppointmentSuccessfully() {

        // ------------------- Arrange -------------------
        Appointment appt = validAppointment("appt-1");
        LocalDateTime newTime = LocalDateTime.now().plusDays(60);

        // ------------------- Act -------------------
        appt.reschedule(new AppointmentTime(newTime));

        // ------------------- Assert -------------------
        assertThat(appt.time().value()).isEqualTo(newTime);
        assertThat(appt.status()).isEqualTo(AppointmentStatus.SCHEDULED);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenReschedulingCancelledAppointment() {

        // ------------------- Arrange -------------------
        Appointment appt = validAppointment("appt-1");
        appt.cancel();
        assertThat(appt.status()).isEqualTo(AppointmentStatus.CANCELLED);

        // ------------------- Act + Assert -------------------
        // Business rule: only SCHEDULED appointments can be rescheduled.
        assertThrows(RuntimeException.class,
                () -> appt.reschedule(new AppointmentTime(LocalDateTime.now().plusDays(1))));
    }

    @Test
    void shouldThrowRuntimeExceptionWhenAppointmentTimeIsInThePast() {

        // ------------------- Act + Assert -------------------
        // Business rule: AppointmentTime(past) → RuntimeException
        assertThrows(RuntimeException.class,
                () -> new AppointmentTime(LocalDateTime.now().minusDays(1)));
    }

    @Test
    void shouldReturnCorrectValueFromAccessors() {

        // ------------------- Arrange -------------------
        Appointment appt = validAppointment("appt-1");

        // ------------------- Assert -------------------
        // Convenience value accessors
        assertThat(appt.doctorIdValue()).isEqualTo("doctor-1");
        assertThat(appt.patientIdValue()).isEqualTo("patient-1");
        assertThat(appt.timeValue()).isEqualTo(FUTURE);
    }

    @Test
    void shouldCreateAppointmentIdAsRecord() {

        // ------------------- Act -------------------
        AppointmentId id1 = new AppointmentId("appt-abc");
        AppointmentId id2 = new AppointmentId("appt-abc");
        AppointmentId id3 = new AppointmentId("appt-xyz");

        // ------------------- Assert -------------------
        // Records implement equals/hashCode automatically.
        assertThat(id1).isEqualTo(id2);
        assertThat(id1).isNotEqualTo(id3);
        assertThat(id1.value()).isEqualTo("appt-abc");
    }

    @Test
    void shouldCreateDoctorRefPatientRefBillingRefAsRecords() {

        // ------------------- Act -------------------
        DoctorRef  d = new DoctorRef("doc-1");
        PatientRef p = new PatientRef("pat-1");
        BillingRef b = new BillingRef("bil-1");

        // ------------------- Assert -------------------
        assertThat(d.value()).isEqualTo("doc-1");
        assertThat(p.value()).isEqualTo("pat-1");
        assertThat(b.value()).isEqualTo("bil-1");
    }
}
