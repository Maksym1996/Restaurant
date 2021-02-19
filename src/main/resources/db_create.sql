-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
set names utf8;
-- -----------------------------------------------------
-- Schema Restaurant
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `Restaurant` ;

-- -----------------------------------------------------
-- Schema Restaurant
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `Restaurant` DEFAULT CHARACTER SET utf8 ;
-- -----------------------------------------------------
-- Schema restaurant
-- -----------------------------------------------------
USE `Restaurant` ;

-- -----------------------------------------------------
-- Table `Restaurant`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Restaurant`.`user` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(45),
  `first_name` VARCHAR(45),
  `last_name` VARCHAR(45),
  `password` VARCHAR(45),
  `phone_number` VARCHAR(13) NOT NULL,
  `role` VARCHAR(45) NOT NULL DEFAULT 'CLIENT',
  `registered` VARCHAR(6) NOT NULL, 
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE,
  UNIQUE INDEX `phone_number_UNIQUE` (`phone_number` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Restaurant`.`product`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Restaurant`.`product` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `price` INT NOT NULL,
  `description` MEDIUMTEXT NOT NULL,
  `image_link` MEDIUMTEXT NOT NULL,
  `category` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Restaurant`.`order`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Restaurant`.`orders` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `order_date` VARCHAR(45) NULL,
  `closing_date` VARCHAR(45) NULL,
  `state` VARCHAR(45) NOT NULL,
  `address` VARCHAR(80) NOT NULL,
  `user_id` INT NOT NULL,
  `sum` VARCHAR(10) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `user_id_idx` (`user_id` ASC) VISIBLE,
  CONSTRAINT `user_id`
    FOREIGN KEY (`user_id`)
    REFERENCES `Restaurant`.`user` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Restaurant`.`order_has_product`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Restaurant`.`order_has_product` (
  `order_id` INT NOT NULL,
  `product_id` INT NOT NULL,
  `count` INT NOT NULL,
  `price` INT NOT NULL,
  INDEX `product_id_idx` (`product_id` ASC) VISIBLE,
  INDEX `order_id_idx` (`order_id` ASC) VISIBLE,
  CONSTRAINT `product_id`
    FOREIGN KEY (`product_id`)
    REFERENCES `Restaurant`.`product` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `order_id`
    FOREIGN KEY (`order_id`)
    REFERENCES `Restaurant`.`orders` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE VIEW orderView
AS SELECT * FROM orders o, order_has_product ohp
WHERE o.id = ohp.order_id;
