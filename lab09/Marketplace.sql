/************************************************************************************
* Create the database named marketplace, all of its tables, and the marketplace user
*************************************************************************************/

-- Create marketplace database

DROP DATABASE IF EXISTS marketplace;

CREATE DATABASE marketplace;

-- Create tables and set content

USE marketplace;

CREATE TABLE UserInfo (
  Username varchar(15) NOT NULL PRIMARY KEY,
  Pbkdf2Hash varchar(200) NOT NULL
);
  
INSERT INTO UserInfo VALUES ('alice', 'PBKDF2WithHmacSHA512:100000:A6kJSvRyVyADUlKum2UMhk0OxU5kGz5zqMaOklLQrWo2PVD4D9rXNqlUQIdoujWnJKKPhJEfLvYKXFDHk6PC8A==:be0X2N7mcg/m0oxk79SPlEBYJtUAR+Hfky9NyjILawk='),
                            ('bob', 'PBKDF2WithHmacSHA512:100000:UC7b0kdr35kVDsfIAMYvZyg8iWWTkHRXia9ZVvoR9AxMPxR06AJVQBrKENdvSCNY3X0vgyHR5L7TI7wFe1Dqfg==:zXXNaLGw5008dBeG4Oi6Zrb7ZpqKMxd51sV9hl7PxvM='),
							('donald', 'PBKDF2WithHmacSHA512:100000:yeukX8nk0vQJszfihTvXUtep44QV42onNdI8TarfDAswnYb1HO4ZMQ2q2Df6WpbTSFVqv17fj/76/UOBSuaj0A==:9dZbBL0UNICwLQOvNBRf/3spiJ2eRKh+AmNG+0Kjp+s='),
						    ('john', 'PBKDF2WithHmacSHA512:100000:FX6Me8UamCrYiafwoVOa4RY8yQDt88IlKUQM2afLc3DBdvUYqwQdL+fB+O8el9ZchdRNiOqbj/ZqEBWD7F22Ew==:PdNld6qcaKVEc/nZFNt90RfUEejuoey74jvG+Izy1NY='),
						    ('luke', 'PBKDF2WithHmacSHA512:100000:Qb2r0ep0HI42KmEZ6xa8EvHKWoA4x+Ye7iyP1f1ykCrGTrq/KhRH/JnAjPRVQqgHrdia5rg3bsKoUBwCEmmR9g==:daBLXLCiyJfKJVz+7a/0dsG9mtTgAgBBofiDc7l5koc='),
                            ('robin', 'PBKDF2WithHmacSHA512:100000:vx0EYPm/hLEs5aqsq12jRXEjZmCwjSFDa4VD1JU0Omk3P1GcZ+juc9mrwO4z6uf0xJhUqwmYthTEwytKZ+Hgfw==:msI5XVAXKf35S3cnAqp0QnUQ2hBfNfM0pU8+JlIk3fc='),
                            ('snoopy', 'PBKDF2WithHmacSHA512:100000:GOZNnVcF/czUpuHxAyoc7+DfKKJCOn4cTHZ73j/fKodn/7hypc20gWUC7PFEyvYrtiOrhFyv1xID9TWgxTDH+w==:7Gat8q4mLqM6Qj7sRJ/LLEHI7Kkbq9bsuuXxEgD8PE8=');
                          
CREATE TABLE UserRole (   
  Username VARCHAR(15) NOT NULL,
  Rolename VARCHAR(15) NOT NULL,

  PRIMARY KEY (Username, Rolename)
);
  
INSERT INTO UserRole VALUES ('alice', 'sales'),
							('bob', 'burgerman'),
                            ('donald', 'productmanager'),
                            ('john', 'sales'),
                            ('luke', 'productmanager'),
                            ('robin', 'marketing'),
                            ('snoopy', 'productmanager');
                                
CREATE TABLE Product (
    ProductID INT NOT NULL AUTO_INCREMENT,
    Code VARCHAR(10) NOT NULL DEFAULT '',
    Description VARCHAR(100) NOT NULL DEFAULT '',
    Price DECIMAL(9,2) NOT NULL DEFAULT '0.00',
    Username varchar(15) NOT NULL,
  
    PRIMARY KEY (ProductID),
    FOREIGN KEY (Username) REFERENCES UserInfo(Username)
);
  
INSERT INTO Product VALUES 
  (1, '0001', 'DVD Life of Brian - used, some scratches but still works', 5.95, 'donald'),
  (2, '0002', 'Ferrari F50 - red, 43000 km, no accidents', 250000.00, 'luke'),
  (3, '0003', 'Commodore C64 - used, the best computer ever built', 444.95, 'luke'),
  (4, '0004', 'Printed Software-Security script - brand new', 10.95, 'donald');

CREATE TABLE Purchase (
    PurchaseID INT NOT NULL AUTO_INCREMENT,
    Firstname VARCHAR(50) NOT NULL DEFAULT '',
    Lastname VARCHAR(50) NOT NULL DEFAULT '',
    CreditCardNumber VARCHAR(100) NOT NULL DEFAULT '',
    TotalPrice DECIMAL(10,2) NOT NULL DEFAULT '0.00',
  
    PRIMARY KEY (PurchaseID)
);

INSERT INTO Purchase VALUES (1, 'Ferrari', 'Driver', '1111 2222 3333 4444', 250000.00),
                            (2, 'C64', 'Freak', '1234 5678 9012 3456', 444.95),
                            (3, 'Script', 'Lover', '5555 6666 7777 8888', 10.95);
                            
-- Create marketplace user and set rights

USE mysql;

DROP USER IF EXISTS 'marketplace'@'localhost';

CREATE USER 'marketplace'@'localhost' IDENTIFIED BY 'marketplace';

GRANT SELECT         ON `marketplace`.* TO 'marketplace'@'localhost';
GRANT UPDATE         ON `marketplace`.`UserInfo` TO 'marketplace'@'localhost';
GRANT INSERT, DELETE, UPDATE ON `marketplace`.`Product` TO 'marketplace'@'localhost';
GRANT INSERT, DELETE ON `marketplace`.`Purchase` TO 'marketplace'@'localhost';
