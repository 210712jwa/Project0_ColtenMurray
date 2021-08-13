DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS client;

CREATE TABLE client (
	id INTEGER PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(255) NOT NULL,
	age INTEGER DEFAULT 0
);

CREATE TABLE account (
	id INTEGER PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(255) NOT NULL,
	balance INTEGER NOT NULL,
	client_id INTEGER NOT NULL,
	CONSTRAINT `fk_account_client` FOREIGN KEY (client_id) REFERENCES client(id) ON DELETE CASCADE
);



INSERT INTO client 
(name, age) 
VALUES
('Jane Doe', 30),
('Jack Daniel', 45);

INSERT INTO account (name, balance, client_id) 
VALUES 
('Checking', 500, 1),
('Saving', 2500, 1),
('Checking', 600, 2);
