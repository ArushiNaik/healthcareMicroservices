
CREATE TABLE patient (
                         id VARCHAR(255) PRIMARY KEY,
                         first_name VARCHAR(255),
                         last_name VARCHAR(255),
                         phone_number VARCHAR(255),
                         email VARCHAR(255),
                         date_of_birth DATE,
                         status VARCHAR(50),

                         country VARCHAR(255),
                         city VARCHAR(255),
                         postal_code VARCHAR(255),
                         street_name VARCHAR(255),
                         street_number INTEGER,

                         health_card_num VARCHAR(255),
                         expiry_date DATE
);

INSERT INTO patient VALUES
                        ('p1','John','Doe','123','john@mail.com','2000-01-01','ACTIVE','CA','Montreal','H1A1A1','Main',10,'DOES90101516','2027-01-01'),
                        ('p2','Anna','Smith','124','anna@mail.com','1998-02-02','ACTIVE','CA','Toronto','H2B2B2','King',20,'SMIT89761235','2029-01-01'),
                        ('p3','Mike','Brown','125','mike@mail.com','1995-03-03','ACTIVE','CA','Vancouver','H3C3C3','West',30,'BROW33329867','2026-06-01'),
                        ('p4','Sara','Lee','126','sara@mail.com','1997-04-04','INACTIVE','CA','Ottawa','H4D4D4','East',40,'LEEM44569187','2028-12-01'),
                        ('p5','Tom','White','127','tom@mail.com','1999-05-05','ACTIVE','CA','Quebec','H5E5E5','North',50,'WHIT55680198','2027-03-01');

UPDATE patient
SET status = 'STABLE'
WHERE status = 'ACTIVE';

UPDATE patient
SET status = 'STABLE'
WHERE status = 'INACTIVE';

UPDATE patient
SET status = 'CRITICAL'
WHERE id = 'p2';
