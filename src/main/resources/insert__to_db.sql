SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;


-- -----------------------------------------------------
-- Data for table `Restaurant`.`user`
-- -----------------------------------------------------
START TRANSACTION;
USE `Restaurant`;
INSERT INTO `Restaurant`.`user` (`id`, `email`, `first_name`, `last_name`, `password`, `phone_number`, `role`, `registered`) VALUES (1, 'kordonets1996@ukr.net', 'Максим', 'Кордонец', '36d2e385ff8453a66347bf048f11668c', '0969055386', 'MANAGER', 'true');
INSERT INTO `Restaurant`.`user` (`id`, `email`, `first_name`, `last_name`, `password`, `phone_number`, `role`, `registered`) VALUES (2, 'Povar@ukr.net', 'Повар', 'Куховаров', '6bb19089370f5bb5478f7ec1b337f255', '0969055385', 'COOK', 'true');
INSERT INTO `Restaurant`.`user` (`id`, `email`, `first_name`, `last_name`, `password`, `phone_number`, `role`, `registered`) VALUES (3, 'Courier@ukr.net', 'Курьер', 'Доставщиков', '6bb19089370f5bb5478f7ec1b337f255', '0969055384', 'COURIER', 'true');
INSERT INTO `Restaurant`.`user` (`id`, `email`, `first_name`, `last_name`, `password`, `phone_number`, `role`, `registered`) VALUES (4, 'kordonetsmax@gmail.com', 'Клиент', 'Посетитович', '6bb19089370f5bb5478f7ec1b337f255', '0969055383', 'CLIENT', 'true');
INSERT INTO `Restaurant`.`user` (`id`, `email`, `first_name`, `last_name`, `password`, `phone_number`, `role`, `registered`) VALUES (5, 'maxkorodnets@gmail.com', 'Админ', 'Распорядитович', '6bb19089370f5bb5478f7ec1b337f255', '0969055382', 'ADMIN', 'true');

COMMIT;



-- -----------------------------------------------------
-- Data for table `Restaurant`.`product`
-- -----------------------------------------------------
START TRANSACTION;
USE `Restaurant`;
INSERT INTO `Restaurant`.`product` (`id`, `name`, `price`, `description`, `image_link`, `category`) VALUES (1, 'Паперони', 320, 'Сыр, мясо и паперони', 'https://raw.githubusercontent.com/Maksym1996/Image_Restaurant/main/Paperonni.jpg', 'Pizza');
INSERT INTO `Restaurant`.`product` (`id`, `name`, `price`, `description`, `image_link`, `category`) VALUES (2, 'Маргатира', 250, 'Сыр, помидоры', 'https://raw.githubusercontent.com/Maksym1996/Image_Restaurant/main/Margarita.jpg', 'Pizza');

INSERT INTO `Restaurant`.`product` (`id`, `name`, `price`, `description`, `image_link`, `category`) VALUES (3, 'Кока-кола', 20, 'Безалкогольный напиток, 0.5л', 'https://raw.githubusercontent.com/Maksym1996/Image_Restaurant/main/Coca-Cola05.jpg', 'Drinks');
INSERT INTO `Restaurant`.`product` (`id`, `name`, `price`, `description`, `image_link`, `category`) VALUES (4, 'Крем-сода', 18, 'Безалкогольный напиток, 0.5л', 'https://raw.githubusercontent.com/Maksym1996/Image_Restaurant/main/krem-soda.jpg', 'Drinks');

INSERT INTO `Restaurant`.`product` (`id`, `name`, `price`, `description`,  `image_link`, `category`) VALUES (5, 'Чизбургер', 30, 'Булка, котлета, сыр', 'https://raw.githubusercontent.com/Maksym1996/Image_Restaurant/main/chizburger.jpg', 'Burger');
INSERT INTO `Restaurant`.`product` (`id`, `name`, `price`, `description`,  `image_link`, `category`) VALUES (6, 'Гамбургер', 26, 'Булка, котлета', 'https://raw.githubusercontent.com/Maksym1996/Image_Restaurant/main/gamburger.jpg', 'Burger');

COMMIT;


