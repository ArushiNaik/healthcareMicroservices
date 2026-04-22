CREATE TABLE appointment (
                             id VARCHAR(255) PRIMARY KEY,

                             doctor_id VARCHAR(255),
                             patient_id VARCHAR(255),
                             billing_id VARCHAR(255),

                             time TIMESTAMP NOT NULL,
                             status VARCHAR(50)
);

INSERT INTO appointment (id, doctor_id, patient_id, billing_id, time, status) VALUES
                                                                                  ('a1','11111111-1111-1111-1111-111111111111','p1','b1','2026-04-20 10:00:00','SCHEDULED'),
                                                                                  ('a2','22222222-2222-2222-2222-222222222222','p2','b2','2026-04-11 11:00:00','SCHEDULED'),
                                                                                  ('a3','33333333-3333-3333-3333-333333333333','p3','b3','2026-04-12 09:30:00','COMPLETED'),
                                                                                  ('a4','44444444-4444-4444-4444-444444444444','p4','b4','2026-04-13 14:00:00','CANCELLED'),
                                                                                  ('a5','55555555-5555-5555-5555-555555555555','p5','b5','2026-04-14 16:00:00','SCHEDULED');
UPDATE appointment
SET
    status = 'SCHEDULED',
    time = DATEADD('MONTH', 3, time);