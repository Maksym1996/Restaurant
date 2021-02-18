

CREATE TABLE IF NOT EXISTS product(
id INTEGER NOT NULL AUTO_INCREMENT,
name VARCHAR(45) NOT NULL,
price INTEGER NOT NULL,
description TEXT NOT NULL,
image_link TEXT NOT NULL,
category VARCHAR(45) NOT NULL,
PRIMARY KEY (id),
UNIQUE (id),
UNIQUE (name));


CREATE TABLE IF NOT EXISTS user(
id INTEGER NOT NULL AUTO_INCREMENT,
email VARCHAR(45),
first_name VARCHAR(45),
last_name VARCHAR(45),
password VARCHAR(45),
phone_number VARCHAR(10) NOT NULL,
role VARCHAR(45) NOT NULL DEFAULT 'CLIENT',
registered VARCHAR(6) NOT NULL,
PRIMARY KEY (id), UNIQUE (id),
UNIQUE (email),UNIQUE (phone_number));


CREATE TABLE IF NOT EXISTS orders (
  id INTEGER NOT NULL AUTO_INCREMENT,
  order_date VARCHAR(45) NULL,
  closing_date VARCHAR(45) NULL,
  state VARCHAR(45) NOT NULL,
  address VARCHAR(80) NOT NULL,
  user_id INTEGER NOT NULL,
  sum VARCHAR(10) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT user_id
    FOREIGN KEY (user_id)
    REFERENCES user (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE);


CREATE TABLE IF NOT EXISTS order_has_product (
  order_id INTEGER NOT NULL,
  product_id INTEGER NOT NULL,
  count INTEGER NOT NULL,
  price INTEGER NOT NULL,
  CONSTRAINT product_id
    FOREIGN KEY (product_id)
    REFERENCES product(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT order_id
    FOREIGN KEY (order_id)
    REFERENCES orders(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE);

CREATE VIEW IF NOT EXISTS orderView 
AS SELECT * FROM orders o, order_has_product ohp
WHERE o.id = ohp.order_id;

DELETE FROM user;
DELETE FROM product;
DELETE FROM orders;
DELETE FROM order_has_product;

INSERT INTO product VALUES (1, 'Paperonni', 320, 'Сыр, мясо и паперони', 'https://raw.githubusercontent.com/Maksym1996/Image_Restaurant/main/Paperonni.jpg', 'Pizza');
INSERT INTO product VALUES (2, 'Margarita', 250, 'Сыр, помидоры', 'https://raw.githubusercontent.com/Maksym1996/Image_Restaurant/main/Margarita.jpg', 'Pizza');

INSERT INTO product VALUES (3, 'Coca-cola', 20, 'Безалкогольный напиток, 0.5л', 'https://raw.githubusercontent.com/Maksym1996/Image_Restaurant/main/Coca-Cola05.jpg', 'Drinks');
INSERT INTO product VALUES (4, 'Crem-soda', 18, 'Безалкогольный напиток, 0.5л', 'https://raw.githubusercontent.com/Maksym1996/Image_Restaurant/main/krem-soda.jpg', 'Drinks');

INSERT INTO product VALUES (5, 'Chisburger', 30, 'Булка, котлета, сыр', 'https://raw.githubusercontent.com/Maksym1996/Image_Restaurant/main/chizburger.jpg', 'Burger');
INSERT INTO product VALUES (6, 'Gamburger', 26, 'Булка, котлета', 'https://raw.githubusercontent.com/Maksym1996/Image_Restaurant/main/gamburger.jpg', 'Burger');
	
INSERT INTO user VALUES (1, 'kordonets1996@ukr.net', 'Максим', 'Кордонец', '36d2e385ff8453a66347bf048f11668c', '0969055386', 'MANAGER', 'true');
INSERT INTO user VALUES (2, 'Povar@ukr.net', 'Повар', 'Куховаров', '6bb19089370f5bb5478f7ec1b337f255', '0969055385', 'COOK', 'true');
INSERT INTO user VALUES (3, 'Courier@ukr.net', 'Курьер', 'Доставщиков', '6bb19089370f5bb5478f7ec1b337f255', '0969055384', 'COURIER', 'true');
INSERT INTO user VALUES (4, 'kordonetsmax@gmail.com', 'Клиент', 'Посетитович', '6bb19089370f5bb5478f7ec1b337f255', '0969055383', 'CLIENT', 'true');
INSERT INTO user VALUES (5, 'maxkorodnets@gmail.com', 'Админ', 'Распорядитович', '6bb19089370f5bb5478f7ec1b337f255', '0969055382', 'ADMIN', 'true');		

INSERT INTO orders(id, order_date, state, address, user_id, sum) VALUES(1, current_timestamp(), 'NEW', 'Плеханово', 2, '500');
INSERT INTO orders(id, order_date, state, address, user_id, sum) VALUES(2, current_timestamp(), 'REJECTED', 'Плеханово', 4, '500');

INSERT INTO order_has_product VALUES(1, 1, 2, 320);
INSERT INTO order_has_product VALUES(1, 2, 1, 250);

INSERT INTO order_has_product VALUES(2, 1, 2, 320);
INSERT INTO order_has_product VALUES(2, 2, 1, 250);



