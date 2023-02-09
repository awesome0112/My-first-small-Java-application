-- MySQL Script generated by MySQL Workbench
-- Thu Feb  9 21:25:04 2023
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema application
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema application
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `application` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `application` ;

-- -----------------------------------------------------
-- Table `application`.`provinces`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `application`.`provinces` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(200) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 4
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `application`.`districts`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `application`.`districts` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(200) NOT NULL,
  `provinceId` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `provinceId` (`provinceId` ASC) VISIBLE,
  CONSTRAINT `districts_ibfk_1`
    FOREIGN KEY (`provinceId`)
    REFERENCES `application`.`provinces` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 12
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `application`.`wards`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `application`.`wards` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(200) NOT NULL,
  `districtId` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `districtId` (`districtId` ASC) VISIBLE,
  CONSTRAINT `wards_ibfk_1`
    FOREIGN KEY (`districtId`)
    REFERENCES `application`.`districts` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 22
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `application`.`customers`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `application`.`customers` (
  `customerNumber` INT NOT NULL AUTO_INCREMENT,
  `customerFirstName` VARCHAR(50) NOT NULL,
  `customerLastName` VARCHAR(50) NOT NULL,
  `phoneNumbers` VARCHAR(10) NOT NULL,
  `userName` VARCHAR(50) NOT NULL,
  `passwords` VARCHAR(50) NOT NULL,
  `salt` VARCHAR(20) NOT NULL DEFAULT 'salt',
  `defaultAddress` INT NOT NULL DEFAULT '0',
  PRIMARY KEY (`customerNumber`),
  UNIQUE INDEX `userName` (`userName` ASC) VISIBLE,
  UNIQUE INDEX `userName_2` (`userName` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 10105
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `application`.`address`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `application`.`address` (
  `addressId` INT NOT NULL AUTO_INCREMENT,
  `provinceId` INT NOT NULL DEFAULT '1',
  `districtId` INT NOT NULL DEFAULT '1',
  `wardId` INT NOT NULL DEFAULT '1',
  `specificAddress` VARCHAR(500) NOT NULL DEFAULT 'undefined',
  `customerNumber` INT NOT NULL,
  PRIMARY KEY (`addressId`),
  INDEX `provinceId` (`provinceId` ASC) VISIBLE,
  INDEX `districtId` (`districtId` ASC) VISIBLE,
  INDEX `wardId` (`wardId` ASC) VISIBLE,
  INDEX `customerNumber` (`customerNumber` ASC) VISIBLE,
  CONSTRAINT `address_ibfk_1`
    FOREIGN KEY (`provinceId`)
    REFERENCES `application`.`provinces` (`id`),
  CONSTRAINT `address_ibfk_2`
    FOREIGN KEY (`districtId`)
    REFERENCES `application`.`districts` (`id`),
  CONSTRAINT `address_ibfk_3`
    FOREIGN KEY (`wardId`)
    REFERENCES `application`.`wards` (`id`),
  CONSTRAINT `address_ibfk_4`
    FOREIGN KEY (`customerNumber`)
    REFERENCES `application`.`customers` (`customerNumber`))
ENGINE = InnoDB
AUTO_INCREMENT = 7
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `application`.`bakers`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `application`.`bakers` (
  `bakerNumber` INT NOT NULL AUTO_INCREMENT,
  `userName` VARCHAR(50) NOT NULL,
  `password` VARCHAR(50) NOT NULL,
  `salt` VARCHAR(20) NOT NULL DEFAULT 'salt',
  `bakerName` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`bakerNumber`),
  UNIQUE INDEX `userName` (`userName` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 40101
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `application`.`productlines`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `application`.`productlines` (
  `productLine` VARCHAR(50) NOT NULL,
  `textDescription` TEXT NULL DEFAULT NULL,
  PRIMARY KEY (`productLine`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `application`.`products`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `application`.`products` (
  `productCode` VARCHAR(15) NOT NULL,
  `productName` VARCHAR(70) NOT NULL,
  `productLine` VARCHAR(50) NOT NULL,
  `productDescription` TEXT NULL DEFAULT NULL,
  `quantityInStock` INT NULL DEFAULT NULL,
  `buyPrice` DECIMAL(10,2) NOT NULL,
  `image` VARCHAR(200) NULL DEFAULT NULL,
  PRIMARY KEY (`productCode`),
  INDEX `productLine` (`productLine` ASC) VISIBLE,
  CONSTRAINT `products_ibfk_1`
    FOREIGN KEY (`productLine`)
    REFERENCES `application`.`productlines` (`productLine`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `application`.`shoppingcarts`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `application`.`shoppingcarts` (
  `cartNumber` INT NOT NULL AUTO_INCREMENT,
  `totalQuantity` INT NOT NULL,
  PRIMARY KEY (`cartNumber`),
  CONSTRAINT `shoppingcarts_ibfk_1`
    FOREIGN KEY (`cartNumber`)
    REFERENCES `application`.`customers` (`customerNumber`))
ENGINE = InnoDB
AUTO_INCREMENT = 10105
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `application`.`cartdetails`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `application`.`cartdetails` (
  `cartNumber` INT NOT NULL AUTO_INCREMENT,
  `productCode` VARCHAR(15) NOT NULL,
  `quantity` INT NOT NULL,
  PRIMARY KEY (`cartNumber`, `productCode`),
  INDEX `productCode` (`productCode` ASC) VISIBLE,
  CONSTRAINT `cartdetails_ibfk_2`
    FOREIGN KEY (`productCode`)
    REFERENCES `application`.`products` (`productCode`),
  CONSTRAINT `cartdetails_ibfk_3`
    FOREIGN KEY (`cartNumber`)
    REFERENCES `application`.`shoppingcarts` (`cartNumber`))
ENGINE = InnoDB
AUTO_INCREMENT = 10105
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `application`.`comments`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `application`.`comments` (
  `productCode` VARCHAR(15) NOT NULL,
  `commentNumber` INT NOT NULL AUTO_INCREMENT,
  `comment` TEXT NOT NULL,
  `customerNumber` INT NOT NULL,
  PRIMARY KEY (`commentNumber`),
  INDEX `productCode` (`productCode` ASC) VISIBLE,
  INDEX `customerNumber` (`customerNumber` ASC) VISIBLE,
  CONSTRAINT `comments_ibfk_1`
    FOREIGN KEY (`productCode`)
    REFERENCES `application`.`products` (`productCode`),
  CONSTRAINT `comments_ibfk_2`
    FOREIGN KEY (`customerNumber`)
    REFERENCES `application`.`customers` (`customerNumber`))
ENGINE = InnoDB
AUTO_INCREMENT = 30113
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `application`.`orders`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `application`.`orders` (
  `orderNumber` INT NOT NULL AUTO_INCREMENT,
  `orderDate` DATETIME NOT NULL,
  `shippedDate` DATETIME NULL DEFAULT NULL,
  `status` VARCHAR(50) NOT NULL,
  `customerNumber` INT NOT NULL,
  `totalPayment` DECIMAL(10,2) NOT NULL,
  `addressId` INT NOT NULL,
  PRIMARY KEY (`orderNumber`),
  INDEX `customerNumber` (`customerNumber` ASC) VISIBLE,
  INDEX `addressId` (`addressId` ASC) VISIBLE,
  CONSTRAINT `orders_ibfk_1`
    FOREIGN KEY (`customerNumber`)
    REFERENCES `application`.`customers` (`customerNumber`),
  CONSTRAINT `orders_ibfk_2`
    FOREIGN KEY (`addressId`)
    REFERENCES `application`.`address` (`addressId`))
ENGINE = InnoDB
AUTO_INCREMENT = 20105
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `application`.`orderdetails`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `application`.`orderdetails` (
  `orderNumber` INT NOT NULL AUTO_INCREMENT,
  `productCode` VARCHAR(15) NOT NULL,
  `quantity` INT NOT NULL,
  PRIMARY KEY (`orderNumber`, `productCode`),
  INDEX `productCode` (`productCode` ASC) VISIBLE,
  CONSTRAINT `orderdetails_ibfk_2`
    FOREIGN KEY (`productCode`)
    REFERENCES `application`.`products` (`productCode`),
  CONSTRAINT `orderdetails_ibfk_3`
    FOREIGN KEY (`orderNumber`)
    REFERENCES `application`.`orders` (`orderNumber`))
ENGINE = InnoDB
AUTO_INCREMENT = 20105
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `application`.`taken_orders`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `application`.`taken_orders` (
  `bakerNumber` INT NOT NULL,
  `orderNumber` INT NOT NULL,
  PRIMARY KEY (`bakerNumber`, `orderNumber`),
  INDEX `orderNumber` (`orderNumber` ASC) VISIBLE,
  CONSTRAINT `taken_orders_ibfk_1`
    FOREIGN KEY (`bakerNumber`)
    REFERENCES `application`.`bakers` (`bakerNumber`),
  CONSTRAINT `taken_orders_ibfk_2`
    FOREIGN KEY (`orderNumber`)
    REFERENCES `application`.`orders` (`orderNumber`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
