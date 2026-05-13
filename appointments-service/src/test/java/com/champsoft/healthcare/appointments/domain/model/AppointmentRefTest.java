package com.champsoft.healthcare.appointments.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// Domain test → pure business rule testing
// NO Spring, NO Mockito, NO database
class AppointmentRefTest {

    // ---- DoctorRef ----

    @Test
    void shouldCreateDoctorRefSuccessfully() {

        // ------------------- Act -------------------
        DoctorRef ref = new DoctorRef("doctor-1");

        // ------------------- Assert -------------------
        assertThat(ref.value()).isEqualTo("doctor-1");
    }

    @Test
    void shouldCompareDoctorRefsCorrectly() {

        // ------------------- Arrange -------------------
        DoctorRef ref1 = new DoctorRef("doctor-1");
        DoctorRef ref2 = new DoctorRef("doctor-1");
        DoctorRef ref3 = new DoctorRef("doctor-2");

        // ------------------- Assert -------------------
        // Records implement equals() automatically based on their components.
        assertThat(ref1).isEqualTo(ref2);
        assertThat(ref1).isNotEqualTo(ref3);
        assertThat(ref1.hashCode()).isEqualTo(ref2.hashCode());
    }

    // ---- PatientRef ----

    @Test
    void shouldCreatePatientRefSuccessfully() {

        // ------------------- Act -------------------
        PatientRef ref = new PatientRef("patient-1");

        // ------------------- Assert -------------------
        assertThat(ref.value()).isEqualTo("patient-1");
    }

    @Test
    void shouldComparePatientRefsCorrectly() {

        // ------------------- Arrange -------------------
        PatientRef ref1 = new PatientRef("patient-1");
        PatientRef ref2 = new PatientRef("patient-1");
        PatientRef ref3 = new PatientRef("patient-2");

        // ------------------- Assert -------------------
        assertThat(ref1).isEqualTo(ref2);
        assertThat(ref1).isNotEqualTo(ref3);
        assertThat(ref1.hashCode()).isEqualTo(ref2.hashCode());
    }

    // ---- BillingRef ----

    @Test
    void shouldCreateBillingRefSuccessfully() {

        // ------------------- Act -------------------
        BillingRef ref = new BillingRef("billing-1");

        // ------------------- Assert -------------------
        assertThat(ref.value()).isEqualTo("billing-1");
    }

    @Test
    void shouldCompareBillingRefsCorrectly() {

        // ------------------- Arrange -------------------
        BillingRef ref1 = new BillingRef("billing-1");
        BillingRef ref2 = new BillingRef("billing-1");
        BillingRef ref3 = new BillingRef("billing-2");

        // ------------------- Assert -------------------
        assertThat(ref1).isEqualTo(ref2);
        assertThat(ref1).isNotEqualTo(ref3);
        assertThat(ref1.hashCode()).isEqualTo(ref2.hashCode());
    }

    // ---- Cross-type inequality ----

    @Test
    void shouldNotBeEqualAcrossDifferentRefTypes() {

        // ------------------- Arrange -------------------
        // Even with the same string value, DoctorRef and PatientRef are different types.
        DoctorRef  doctor  = new DoctorRef("ref-1");
        PatientRef patient = new PatientRef("ref-1");
        BillingRef billing = new BillingRef("ref-1");

        // ------------------- Assert -------------------
        // Records use the class type in equals(), so these must not be equal.
        assertThat(doctor).isNotEqualTo(patient);
        assertThat(doctor).isNotEqualTo(billing);
        assertThat(patient).isNotEqualTo(billing);
    }
}
