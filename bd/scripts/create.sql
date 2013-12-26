SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

CREATE SCHEMA IF NOT EXISTS `amigo_seguro` ;
USE `amigo_seguro` ;

-- -----------------------------------------------------
-- Table `amigo_seguro`.`pais`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `amigo_seguro`.`pais` (
  `id_pais` INT NOT NULL AUTO_INCREMENT ,
  `nombre` VARCHAR(32) NOT NULL ,
  PRIMARY KEY (`id_pais`) ,
  UNIQUE INDEX `idx_pais_nombre` USING BTREE (`nombre` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `amigo_seguro`.`region`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `amigo_seguro`.`region` (
  `id_region` INT NOT NULL AUTO_INCREMENT ,
  `id_pais_FK` INT NOT NULL ,
  `nombre` VARCHAR(64) NOT NULL ,
  PRIMARY KEY (`id_region`) ,
  UNIQUE INDEX `idx_region_nombre` USING BTREE (`nombre` ASC) ,
  INDEX `fk_pais0` (`id_pais_FK` ASC) ,
  CONSTRAINT `fk_pais0`
    FOREIGN KEY (`id_pais_FK` )
    REFERENCES `amigo_seguro`.`pais` (`id_pais` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `amigo_seguro`.`ciudad`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `amigo_seguro`.`ciudad` (
  `id_ciudad` INT NOT NULL AUTO_INCREMENT ,
  `id_region_FK` INT NOT NULL ,
  `nombre` VARCHAR(32) NOT NULL ,
  PRIMARY KEY (`id_ciudad`) ,
  UNIQUE INDEX `idx_ciudad_nombre` USING BTREE (`nombre` ASC) ,
  INDEX `fk_region0` (`id_region_FK` ASC) ,
  CONSTRAINT `fk_region0`
    FOREIGN KEY (`id_region_FK` )
    REFERENCES `amigo_seguro`.`region` (`id_region` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `amigo_seguro`.`comuna`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `amigo_seguro`.`comuna` (
  `id_comuna` INT NOT NULL AUTO_INCREMENT ,
  `id_ciudad_FK` INT NOT NULL ,
  `nombre` VARCHAR(32) NOT NULL ,
  PRIMARY KEY (`id_comuna`) ,
  UNIQUE INDEX `idx_comuna_nombre` USING BTREE (`nombre` ASC) ,
  INDEX `fk_ciudad0` (`id_ciudad_FK` ASC) ,
  CONSTRAINT `fk_ciudad0`
    FOREIGN KEY (`id_ciudad_FK` )
    REFERENCES `amigo_seguro`.`ciudad` (`id_ciudad` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `amigo_seguro`.`usuario`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `amigo_seguro`.`usuario` (
  `id_usuario` INT NOT NULL AUTO_INCREMENT ,
  `id_comuna_FK` INT NULL ,
  `alias` VARCHAR(20) NOT NULL ,
  `contrasena` VARCHAR(16) NOT NULL ,
  `nombre` VARCHAR(30) NOT NULL ,
  `apellido_paterno` VARCHAR(30) NOT NULL ,
  `apellido_materno` VARCHAR(30) NULL ,
  `movil` VARCHAR(20) NULL ,
  `rut` VARCHAR(20) NULL ,
  `correo` VARCHAR(64) NOT NULL ,
  `correo_opcional` VARCHAR(64) NULL ,
  `fecha_nacimiento` DATE NULL ,
  `hombre` BIT(1) NOT NULL DEFAULT default b'1' ,
  `contactar_contactos_de_contactos` BIT(1) NOT NULL DEFAULT default b'0' ,
  `contactar_desconocidos` BIT(1) NOT NULL DEFAULT default b'0' ,
  `publicar_informacion` BIT(1) NOT NULL DEFAULT default b'0' ,
  `clave_validacion` VARCHAR(8) NULL ,
  `direccion` VARCHAR(128) NULL ,
  `foto` VARCHAR(250) NULL ,
  `antecedentes_emergencia` TEXT NULL ,
  `validado` BIT(1) NOT NULL DEFAULT default b'0' ,
  `borrado` BIT(1) NOT NULL DEFAULT default b'0' ,
  PRIMARY KEY (`id_usuario`) ,
  INDEX `fk_comuna0` (`id_comuna_FK` ASC) ,
  CONSTRAINT `fk_comuna0`
    FOREIGN KEY (`id_comuna_FK` )
    REFERENCES `amigo_seguro`.`comuna` (`id_comuna` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `amigo_seguro`.`contacto`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `amigo_seguro`.`contacto` (
  `id_contacto` INT NOT NULL AUTO_INCREMENT ,
  `id_usuario_FK` INT NOT NULL ,
  `id_usuario_contacto_FK` INT NULL ,
  `correo` VARCHAR(64) NOT NULL ,
  `relacion` VARCHAR(32) NOT NULL ,
  PRIMARY KEY (`id_contacto`) ,
  UNIQUE INDEX `idx_contacto` USING BTREE (`id_usuario_FK` ASC, `id_usuario_contacto_FK` ASC) ,
  INDEX `fk_usuario0` (`id_usuario_FK` ASC) ,
  INDEX `fk_usuario1` (`id_usuario_contacto_FK` ASC) ,
  CONSTRAINT `fk_usuario0`
    FOREIGN KEY (`id_usuario_FK` )
    REFERENCES `amigo_seguro`.`usuario` (`id_usuario` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_usuario1`
    FOREIGN KEY (`id_usuario_contacto_FK` )
    REFERENCES `amigo_seguro`.`usuario` (`id_usuario` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `amigo_seguro`.`comunidad`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `amigo_seguro`.`comunidad` (
  `id_comunidad` INT NOT NULL AUTO_INCREMENT ,
  `nombre` VARCHAR(32) NOT NULL ,
  `descripcion` VARCHAR(128) NULL ,
  `fecha_creacion` DATETIME NOT NULL ,
  `latitud` DOUBLE NULL ,
  `longitud` DOUBLE NULL ,
  `cobertura` BIGINT NULL ,
  PRIMARY KEY (`id_comunidad`) ,
  UNIQUE INDEX `idx_comunidad_nombre` USING BTREE (`nombre` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `amigo_seguro`.`comunidad_usuario`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `amigo_seguro`.`comunidad_usuario` (
  `id_comunidad_usuario` INT NOT NULL AUTO_INCREMENT ,
  `id_comunidad_FK` INT NOT NULL ,
  `id_usuario_FK` INT NOT NULL ,
  `es_lider` BIT(1) NULL ,
  `es_administrador` BIT(1) NULL ,
  `fecha_ingreso` DATETIME NOT NULL ,
  PRIMARY KEY (`id_comunidad_usuario`) ,
  UNIQUE INDEX `idx_comunidad_usuario` USING BTREE (`id_comunidad_FK` ASC, `id_usuario_FK` ASC) ,
  INDEX `fk_comunidad0` (`id_comunidad_FK` ASC) ,
  INDEX `fk_usuario2` (`id_usuario_FK` ASC) ,
  CONSTRAINT `fk_comunidad0`
    FOREIGN KEY (`id_comunidad_FK` )
    REFERENCES `amigo_seguro`.`comunidad` (`id_comunidad` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_usuario2`
    FOREIGN KEY (`id_usuario_FK` )
    REFERENCES `amigo_seguro`.`usuario` (`id_usuario` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `amigo_seguro`.`servidor_chat`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `amigo_seguro`.`servidor_chat` (
  `id_servidor_chat` INT NOT NULL AUTO_INCREMENT ,
  `nombre` VARCHAR(32) NOT NULL ,
  `ip` VARCHAR(15) NULL ,
  PRIMARY KEY (`id_servidor_chat`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `amigo_seguro`.`chat`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `amigo_seguro`.`chat` (
  `id_chat` INT NOT NULL AUTO_INCREMENT ,
  `nombre` VARCHAR(32) NOT NULL ,
  `fecha_creacion` DATETIME NOT NULL ,
  `id_usuario_creador_FK` INT NOT NULL ,
  `id_servidor_chat_FK` INT NULL ,
  `id_chat_servidor` BIGINT NULL ,
  PRIMARY KEY (`id_chat`) ,
  INDEX `fk_usuario3` (`id_usuario_creador_FK` ASC) ,
  INDEX `fk_servidor_chat0` (`id_servidor_chat_FK` ASC) ,
  UNIQUE INDEX `id_chat_servidor_UNIQUE` (`id_chat_servidor` ASC) ,
  CONSTRAINT `fk_usuario3`
    FOREIGN KEY (`id_usuario_creador_FK` )
    REFERENCES `amigo_seguro`.`usuario` (`id_usuario` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_servidor_chat0`
    FOREIGN KEY (`id_servidor_chat_FK` )
    REFERENCES `amigo_seguro`.`servidor_chat` (`id_servidor_chat` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `amigo_seguro`.`chat_usuario`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `amigo_seguro`.`chat_usuario` (
  `id_chat_usuario` INT NOT NULL AUTO_INCREMENT ,
  `id_chat_FK` INT NOT NULL ,
  `id_usuario_FK` INT NOT NULL ,
  `fecha_ingreso` DATETIME NOT NULL ,
  `latitud_inicial` DOUBLE NULL ,
  `longitud_inicial` DOUBLE NULL ,
  PRIMARY KEY (`id_chat_usuario`) ,
  UNIQUE INDEX `id_chat_usuario` (`id_chat_FK` ASC, `id_usuario_FK` ASC) ,
  INDEX `fk_usuario4` (`id_usuario_FK` ASC) ,
  INDEX `fk_chat0` (`id_chat_FK` ASC) ,
  CONSTRAINT `fk_usuario4`
    FOREIGN KEY (`id_usuario_FK` )
    REFERENCES `amigo_seguro`.`usuario` (`id_usuario` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_chat0`
    FOREIGN KEY (`id_chat_FK` )
    REFERENCES `amigo_seguro`.`chat` (`id_chat` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `amigo_seguro`.`incidente`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `amigo_seguro`.`incidente` (
  `id_incidente` INT NOT NULL AUTO_INCREMENT ,
  `tipo` TINYINT NOT NULL ,
  `id_usuario_FK` INT NOT NULL ,
  `fecha` DATETIME NOT NULL ,
  `latitud` DOUBLE NOT NULL ,
  `longitud` DOUBLE NULL ,
  `archivos_asociados` TEXT NULL ,
  `id_chat_FK` INT NOT NULL ,
  PRIMARY KEY (`id_incidente`, `id_chat_FK`) ,
  INDEX `fk_usuario5` (`id_usuario_FK` ASC) ,
  CONSTRAINT `fk_usuario5`
    FOREIGN KEY (`id_usuario_FK` )
    REFERENCES `amigo_seguro`.`usuario` (`id_usuario` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `amigo_seguro`.`incidente_usuario`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `amigo_seguro`.`incidente_usuario` (
  `id_incidente_usuario` INT NOT NULL AUTO_INCREMENT ,
  `id_incidente_FK` INT NOT NULL ,
  `id_usuario_FK` INT NOT NULL ,
  `fecha_ingreso` DATETIME NOT NULL ,
  PRIMARY KEY (`id_incidente_usuario`) ,
  UNIQUE INDEX `idx_incidente_usuario` USING BTREE (`id_incidente_FK` ASC, `id_usuario_FK` ASC) ,
  INDEX `fk_usuario6` (`id_usuario_FK` ASC) ,
  INDEX `fk_incidente` (`id_incidente_FK` ASC) ,
  CONSTRAINT `fk_usuario6`
    FOREIGN KEY (`id_usuario_FK` )
    REFERENCES `amigo_seguro`.`usuario` (`id_usuario` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_incidente`
    FOREIGN KEY (`id_incidente_FK` )
    REFERENCES `amigo_seguro`.`incidente` (`id_incidente` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `amigo_seguro`.`usuario_posicion`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `amigo_seguro`.`usuario_posicion` (
  `id_usuario_posicion` INT NOT NULL AUTO_INCREMENT ,
  `id_usuario_FK` INT NOT NULL ,
  `latitud` DOUBLE NOT NULL ,
  `longitud` DOUBLE NOT NULL ,
  `fecha` DATETIME NOT NULL ,
  PRIMARY KEY (`id_usuario_posicion`) ,
  INDEX `fk_usuario7` (`id_usuario_FK` ASC) ,
  CONSTRAINT `fk_usuario7`
    FOREIGN KEY (`id_usuario_FK` )
    REFERENCES `amigo_seguro`.`usuario` (`id_usuario` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `amigo_seguro`.`usuario_parametro`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `amigo_seguro`.`usuario_parametro` (
  `id_usuario_parametro` INT NOT NULL AUTO_INCREMENT ,
  `id_usuario_FK` INT NOT NULL ,
  `parametros` TEXT NULL ,
  PRIMARY KEY (`id_usuario_parametro`) ,
  UNIQUE INDEX `id_usuario_FK_UNIQUE` (`id_usuario_FK` ASC) ,
  INDEX `fk_usuario8` (`id_usuario_FK` ASC) ,
  CONSTRAINT `fk_usuario8`
    FOREIGN KEY (`id_usuario_FK` )
    REFERENCES `amigo_seguro`.`usuario` (`id_usuario` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `amigo_seguro`.`comunidad_tema`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `amigo_seguro`.`comunidad_tema` (
  `id_comunidad_tema` INT NOT NULL AUTO_INCREMENT ,
  `id_comunidad_FK` INT NOT NULL ,
  `id_usuario_creador_FK` INT NOT NULL ,
  `id_chat_FK` INT NULL ,
  `nombre` VARCHAR(30) NOT NULL ,
  `descripcion` VARCHAR(1024) NULL ,
  `fecha` DATETIME NOT NULL ,
  PRIMARY KEY (`id_comunidad_tema`) ,
  INDEX `fk_usuario9` (`id_usuario_creador_FK` ASC) ,
  INDEX `fk_comunidad1` (`id_comunidad_FK` ASC) ,
  INDEX `fk_chat1` (`id_chat_FK` ASC) ,
  CONSTRAINT `fk_usuario9`
    FOREIGN KEY (`id_usuario_creador_FK` )
    REFERENCES `amigo_seguro`.`usuario` (`id_usuario` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_comunidad1`
    FOREIGN KEY (`id_comunidad_FK` )
    REFERENCES `amigo_seguro`.`comunidad` (`id_comunidad` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_chat1`
    FOREIGN KEY (`id_chat_FK` )
    REFERENCES `amigo_seguro`.`chat` (`id_chat` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `amigo_seguro`.`comunidad_tema_usuario`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `amigo_seguro`.`comunidad_tema_usuario` (
  `id_comunidad_tema_usuario` INT NOT NULL AUTO_INCREMENT ,
  `id_comunidad_tema_FK` INT NOT NULL ,
  `id_usuario_FK` INT NOT NULL ,
  `estado` BIT(1) NOT NULL ,
  PRIMARY KEY (`id_comunidad_tema_usuario`) ,
  UNIQUE INDEX `idx_comunidad_tema_usuario` USING BTREE (`id_comunidad_tema_FK` ASC, `id_usuario_FK` ASC) ,
  INDEX `fk_comunidad_tema0` (`id_comunidad_tema_FK` ASC) ,
  INDEX `fk_usuario10` (`id_usuario_FK` ASC) ,
  CONSTRAINT `fk_comunidad_tema0`
    FOREIGN KEY (`id_comunidad_tema_FK` )
    REFERENCES `amigo_seguro`.`comunidad_tema` (`id_comunidad_tema` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_usuario10`
    FOREIGN KEY (`id_usuario_FK` )
    REFERENCES `amigo_seguro`.`usuario` (`id_usuario` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `amigo_seguro`.`mensaje_usuario`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `amigo_seguro`.`mensaje_usuario` (
  `id_mensaje_usuario` INT NOT NULL AUTO_INCREMENT ,
  `id_usuario_FK` INT NOT NULL ,
  `mensaje` TEXT NOT NULL ,
  `fecha` DATETIME NOT NULL ,
  `leido` BIT(1) NOT NULL ,
  PRIMARY KEY (`id_mensaje_usuario`) ,
  INDEX `fk_usuario11` (`id_usuario_FK` ASC) ,
  CONSTRAINT `fk_usuario11`
    FOREIGN KEY (`id_usuario_FK` )
    REFERENCES `amigo_seguro`.`usuario` (`id_usuario` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
