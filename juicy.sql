DROP DATABASE juicy;
CREATE DATABASE juicy;
USE juicy;

CREATE TABLE image (
	id INTEGER NOT NULL AUTO_INCREMENT,
	content BLOB NOT NULL,
	PRIMARY KEY (id)
);

INSERT INTO image VALUES (1, "http://i.imgur.com/DvpvklR.png");
INSERT INTO image VALUES (2, "http://i.imgur.com/DvpvklR.png");
INSERT INTO image VALUES (3, "http://i.imgur.com/DvpvklR.png");

CREATE TABLE user (
	email VARCHAR(255) NOT NULL UNIQUE,
	name VARCHAR(255) NOT NULL UNIQUE,
	passwd VARCHAR(255) NOT NULL,
	imgId INTEGER,
	PRIMARY KEY (email),
	FOREIGN KEY (imgId) REFERENCES image (id)
);

INSERT INTO user VALUES ("zxq@cmu.edu", "zhexinq", "123456", 1);
INSERT INTO user VALUES ("pfj@cmu.edu", "pufanj", "123456", 2);
INSERT INTO user VALUES ("lqc@cmu.edu", "linquanc", "123456", 3);
INSERT INTO user VALUES ("eventCreator@cmu.edu", "Aladin", "9527", 1);
INSERT INTO user VALUES ("anotherEvent@cmu.edu", "Paladin", "9527", 2);
INSERT INTO user VALUES ("yetAnotherEvent@cmu.edu", "Soladin", "9527", 3);


CREATE TABLE event (
	id INTEGER NOT NULL AUTO_INCREMENT,
	creatorEmail VARCHAR(255) NOT NULL,
	name VARCHAR(255) NOT NULL,
	lat REAL,
	lon REAL,
	eventDateTime DATETIME NOT NULL,
	description VARCHAR(255),
	imgId INTEGER, 
	imageContextColor INTEGER,
	titleContextColor INTEGER,
	PRIMARY KEY (id),
	FOREIGN KEY (creatorEmail) REFERENCES user (email),
	FOREIGN KEY (imgId) REFERENCES image (id)
);

INSERT INTO event (creatorEmail, name, lat, lon, eventDateTime, description, imgId, imageContextColor, titleContextColor) 
VALUES ("eventCreator@cmu.edu", "CMU conference", 23.1, -23.5, "2015-5-27 08:25:51", "This is a great event!", 
	3, 1, 2);
INSERT INTO event (creatorEmail, name, lat, lon, eventDateTime, description, imgId, imageContextColor, titleContextColor) 
VALUES ("anotherEvent@cmu.edu", "Juicy Summit", 23.2, -23.4, "2015-9-27 12:25:51", "This is a another great event!", 
	3, 3, 4);
INSERT INTO event (creatorEmail, name, lat, lon, eventDateTime, description, imgId, imageContextColor, titleContextColor) 
VALUES ("yetAnotherEvent@cmu.edu", "Linux workshop", 23.2, -23.5, "2015-1-24 08:25:51", "This is yet another great event!", 
	2, 5, 6);

CREATE TABLE eventUser (
	eventId INTEGER NOT NULL,
	usrEmail VARCHAR(255) NOT NULL,
	FOREIGN KEY (eventId) REFERENCES event (id),
	FOREIGN KEY (usrEmail) REFERENCES user (email)
);
ALTER TABLE eventUser ADD CONSTRAINT uq_eventUser UNIQUE(eventId, usrEmail);

INSERT INTO eventUser VALUES (1, "zxq@cmu.edu");
INSERT INTO eventUser VALUES (2, "zxq@cmu.edu");
INSERT INTO eventUser VALUES (1, "pfj@cmu.edu");
INSERT INTO eventUser VALUES (1, "lqc@cmu.edu");


