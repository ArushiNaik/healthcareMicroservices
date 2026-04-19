CREATE TABLE billing (
                         id VARCHAR(255) PRIMARY KEY,

                         appointment_id VARCHAR(255),
                         patient_id VARCHAR(255),

                         amount DOUBLE PRECISION,
                         description VARCHAR(255),

                         due_date DATE,
                         payment_method VARCHAR(50),
                         status VARCHAR(50)
);

INSERT INTO billing (id, appointment_id, patient_id, amount, description, due_date, payment_method, status) VALUES
                                                                                                                ('b1','a1','p1',120.5,'Consultation','2026-04-20','CASH','PENDING'),
                                                                                                                ('b2','a2','p2',200.0,'Cardio check','2026-04-21','CREDIT_CARD','PAID'),
                                                                                                                ('b3','a3','p3',180.0,'Neuro exam','2026-04-22','INSURANCE','PENDING'),
                                                                                                                ('b4','a4','p4',120.0,'Consultation','2026-04-23','CASH','PENDING'),
                                                                                                                ('b5','a5','p5',150.0,'General checkup','2026-04-24','CREDIT_CARD','PENDING');