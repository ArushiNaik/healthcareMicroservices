CREATE TABLE doctor (
                        id VARCHAR(255) PRIMARY KEY,
                        first_name VARCHAR(255),
                        last_name VARCHAR(255),
                        specialty VARCHAR(255),
                        license_expiry_date DATE,
                        active BOOLEAN
);

INSERT INTO doctor (id, first_name, last_name, specialty, license_expiry_date, active) VALUES
                                                                                           ('11111111-1111-1111-1111-111111111111', 'Sarah', 'Chen', 'Cardiology', '2030-12-31', true),
                                                                                           ('22222222-2222-2222-2222-222222222222', 'Michael', 'Brown', 'Neurology', '2029-06-30', true),
                                                                                           ('33333333-3333-3333-3333-333333333333', 'Aisha', 'Khan', 'Dermatology', '2028-09-15', true),
                                                                                           ('44444444-4444-4444-4444-444444444444', 'James', 'Wilson', 'Orthopedics', '2031-01-10', true),
                                                                                           ('55555555-5555-5555-5555-555555555555', 'Emily', 'Davis', 'Pediatrics', '2027-11-20', true);