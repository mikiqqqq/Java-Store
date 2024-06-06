CREATE TABLE BRAND
(
    ID    NUMBER PRIMARY KEY AUTO_INCREMENT,
    TITLE VARCHAR(255)
);

CREATE TABLE CATEGORY
(
    ID    NUMBER PRIMARY KEY AUTO_INCREMENT,
    TITLE VARCHAR(255)
);

CREATE TABLE PRODUCT_YEAR
(
    ID    NUMBER PRIMARY KEY AUTO_INCREMENT,
    PRODUCT_YEAR  VARCHAR(255)
);

CREATE TABLE PRODUCT
(
    ID                      NUMBER(11) PRIMARY KEY AUTO_INCREMENT,
    IMAGE                   BLOB,
    TITLE                   VARCHAR(41),
    DESCRIPTION             VARCHAR(255),
    PRICE                   DECIMAL,
    QUANTITY                NUMBER,
    BRAND_ID                NUMBER,
    CATEGORY_ID             NUMBER,
    PRODUCT_YEAR_ID                 NUMBER,
    FOREIGN KEY (BRAND_ID) REFERENCES BRAND(ID),
    FOREIGN KEY (CATEGORY_ID) REFERENCES CATEGORY(ID),
    FOREIGN KEY (PRODUCT_YEAR_ID) REFERENCES PRODUCT_YEAR(ID)
);

CREATE TABLE AUTHORIZATION_LEVEL
(
    ID          NUMBER PRIMARY KEY AUTO_INCREMENT,
    TITLE       VARCHAR(255)
);

CREATE TABLE "USER"
(
    ID                  NUMBER(11) PRIMARY KEY AUTO_INCREMENT,
    NAME                VARCHAR(255),
    EMAIL               VARCHAR(255),
    PASSWORD_HASH       VARCHAR(255),
    SALT                VARCHAR(255),
    AUTHORIZATION_LEVEL_ID NUMBER(11),
    FOREIGN KEY (AUTHORIZATION_LEVEL_ID) REFERENCES AUTHORIZATION_LEVEL(ID)
);

CREATE TABLE PAYMENT_TYPE
(
    ID          NUMBER PRIMARY KEY AUTO_INCREMENT,
    TITLE       VARCHAR(255)
);

CREATE TABLE "ORDER"
(
    ID                  NUMBER(11) PRIMARY KEY AUTO_INCREMENT,
    DATE                DATETIME,
    PAYMENT_TYPE_ID     NUMBER,
    CARD_NUMBER         VARCHAR(255),
    EMAIL               VARCHAR(255),
    PHONE_NUMBER        VARCHAR(255),
    ADDRESS             VARCHAR(255),
    MESSAGE             VARCHAR(255),
    STATUS              VARCHAR(255),
    FOREIGN KEY (PAYMENT_TYPE_ID) REFERENCES PAYMENT_TYPE(ID)
);

CREATE TABLE ORDER_ITEM
(
    ID                  NUMBER PRIMARY KEY AUTO_INCREMENT,
    QUANTITY            NUMBER,
    ORDER_ID            NUMBER,
    PRODUCT_ID          NUMBER,
    FOREIGN KEY (ORDER_ID) REFERENCES "ORDER"(ID),
    FOREIGN KEY (PRODUCT_ID) REFERENCES PRODUCT(ID)
);

INSERT INTO BRAND (TITLE) VALUES ('Asus'), ('Razer'), ('Samsung'), ('Apple'),
                                 ('Xiaomi'), ('LG'), ('Speedlink'), ('Noa'),
                                 ('Logitech'), ('Fenix'), ('Lenovo'), ('Vivax');

INSERT INTO CATEGORY (TITLE) VALUES ('Mice'), ('Audio'), ('TV'), ('Laptops');

INSERT INTO PRODUCT_YEAR (PRODUCT_YEAR) VALUES ('2024'), ('2023'), ('2022'), ('2021'), ('2020');

INSERT INTO PRODUCT (TITLE, DESCRIPTION, BRAND_ID, PRICE, CATEGORY_ID, PRODUCT_YEAR_ID)
VALUES ('Speedlink Miš S50', 'Specifikacije za Speedlink Miš S50', 7, 250.00, 1, 1),
        ('Razer Miš T350', 'Specifikacije za Razer Miš T350', 2, 360.00, 1, 1),
        ('PC Fenix Ultima R9-5950X', 'Specifikacije za Stolno računalo PC Fenix Ultima R9-5950X, 64GB, 2TB M.2, RTX2060 8GB, PWS 1000W', 10, 23560.99, 1, 1),
        ('Lenovo ThinkStation Xeon W-2133', 'Specifikacije za Stolno računalo Lenovo ThinkStation Xeon W-2133/64GB/512GB/Quadro P4000/W10P', 11, 2350.96, 1, 1),
        ('Lenovo V50t Gen2','Specifikacije za Lenovo V50t Gen2 i3/8GB/256GB/IntHD/W10P/5god', 11, 5665.29, 2, 2),
        ('Lenovo M70s SFF G6400','Specifikacije za za Lenovo M70s SFF G6400/4GB/256GB/IntHD/W10P', 11, 4758.84, 2, 2),
        ('Iphone SE', 'Specifikacije za Iphone SE', 4, 8459.29, 2, 2),
        ('MacBook Air', 'Specifikacije za MacBook Air', 4, 11239.59, 2, 2),
        ('iPad Pro','Specifikacije za iPad Pro', 4, 3480, 3, 3),
        ('Vivax LED TV-49S60T2S2SM', 'Novi Smart TV s Android sustavom 7.1 omogućava povezanost putem interneta ili društvenih mreža, a sve to na velikom ekranu.', 12, 2359.29, 3, 3),
        ('Vivax IMAGO LED TV-32LE95T2', 'Posebno ergonomski dizajniran televizor koji umjesto klasičnog postolja ima nogice koje doprinose ukupnoj eleganciji televizora.', 12, 3380.79, 3, 3),
        ('Xiaomi 11 Lite 5G NE', 'Specifikacije za Xiaomi 11 Lite 5G NE', 5, 6679.59, 3, 3),
        ('Noa Element Mobitel', 'Specifikacije za Noa Element Mobitel', 8, 899.49, 3, 3),
        ('LOGITECH H820E HEADSET', 'Bežične slušalice za poslovne razgovore', 9, 349.89, 4, 4),
        ('Galaxy Z Fold3 5G', 'Specifkacije za Galaxy Z Fold3 5G', 3, 8129.59, 4, 4),
        ('LG 75" (189 cm) 4K HDR Smart Nano Cell TV', 'Čiste boje (Pure Colors) u razlučivosti Real 4K, Tehnologija NanoCell, 4K procesor LG α5 Gen5 AI', 6, 5579.29, 4, 4),
        ('16" ProArt Studiobook Pro 16 OLED', 'Windows 11 Pro, Intel® Core™ i9-12900H processor, NVIDIA RTX™ A3000 12GB graphics', 1, 8888.88, 4, 4);







INSERT INTO PAYMENT_TYPE (TITLE) VALUES ('CARD'), ('CASH');

INSERT INTO AUTHORIZATION_LEVEL (TITLE) VALUES ('ADMIN'), ('CUSTOMER');