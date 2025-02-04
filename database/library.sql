CREATE DATABASE library;
USE library;
CREATE TABLE admin (
	id INT AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    question VARCHAR(225) NOT NULL,
    answer VARCHAR(225) NOT NULL,
    date DATE,
    PRIMARY KEY (id)
);

CREATE TABLE book (
	isbn VARCHAR(225) NOT NULL,
    title VARCHAR(225) NOT NULL,
    author VARCHAR(225) NOT NULL,
    genre VARCHAR(225) NOT NULL,
    image VARCHAR(500),
    pub_date DATE,
    quantity INT NOT NULL,
	remain INT NOT NULL,
	issued INT NOT NULL,
    PRIMARY KEY(isbn)
);

CREATE TABLE user (
	msv VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    phonenumber VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
	image VARCHAR(500),
	createdDate Date,
	password VARCHAR(255),
	question VARCHAR(255),
	answer VARCHAR(255),
    PRIMARY KEY (msv)
    );

CREATE TABLE issue (
	msv VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    isbn VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOt null,
    issueDate DATE,
    dueDate DATE,
    returnDate DATE,
	id INT AUTO_INCREMENT PRIMARY KEY,
	CONSTRAINT fk_msv FOREIGN KEY (msv) REFERENCES user(msv) ON DELETE CASCADE
);

