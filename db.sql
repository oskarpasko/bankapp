CREATE TABLE Client
(
	client_nr VARCHAR(6) PRIMARY KEY NOT NULL UNIQUE CHECK(char_length(client_nr) = 6),
    client_password VARCHAR(100) NOT NULL,
    client_fname VARCHAR(100) NOT NULL,
    client_lname VARCHAR(100) NOT NULL,
    client_balance FLOAT NOT NULL CHECK (client_balance >= 0)
);

INSERT INTO Client values('123456', 'adminadmin', 'Admin', 'Admin', 1000.0);
INSERT INTO Client values('654321', 'testtest', 'Test', 'Test', 0.0);
INSERT INTO Client values('923860', 'oskarpasko', 'Oskar', 'Paśko', 50.0);
INSERT INTO Client values('015348', 'elizatworkowska', 'Eliza', 'Tworkowska', 100.0);

CREATE TABLE Card
(
    card_nr VARCHAR(16) PRIMARY KEY NOT NULL UNIQUE CHECK(char_length(card_nr)=16),
    card_term_data DATE NOT NULL,
    card_cvc_number VARCHAR(3) NOT NULL CHECK(char_length(cvc_number)=3),
    card_type ENUM('Debetowa', 'Kredytowa'),
    client_nr VARCHAR(6) NOT NULL,
    FOREIGN KEY (client_nr) REFERENCES Client (client_nr)
);

INSERT INTO Card values('1234567890123456', '2026-03-02', '123', 'Debetowa', '123456');
INSERT INTO Card values('6543210987654321', '2023-02-02', '321', 'Kredytowa', '123456');
INSERT INTO Card values('1957463518203058', '2023-12-12', '000', 'Kredytowa', '123456');
INSERT INTO Card values('3947561056372946', '2025-12-11', '789', 'Debetowa', '923860');
INSERT INTO Card values('2635000016253729', '2023-09-12', '946', 'Kredytowa', '654321');
INSERT INTO Card values('1221344345545665', '2024-06-01', '734', 'Debetowa', '654321');
INSERT INTO Card values('0192837465657489', '2024-05-05', '667', 'Debetowa', '923860');
INSERT INTO Card values('0293650142399990', '2023-11-11', '912', 'Debetowa', '015348');
INSERT INTO Card values('1122334455667788', '2027-02-02', '112', 'Kredytowa', '015348');
INSERT INTO Card values('9102910291029102', '2026-02-08', '520', 'Kredytowa', '015348');