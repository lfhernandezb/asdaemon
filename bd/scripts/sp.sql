DELIMITER $$
CREATE PROCEDURE `carga_datos`()
BEGIN
  DECLARE id_pais, id_region, id_ciudad, id_comuna INT DEFAULT FALSE;
  DECLARE aux INT DEFAULT FALSE;
  DECLARE done INT DEFAULT FALSE;
	      
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

  DECLARE EXIT HANDLER FOR SQLEXCEPTION
  BEGIN
      -- ROLLBACK;
      SET done = TRUE;
  END; 

  -- START TRANSACTION;
  
  INSERT INTO pais (nombre)
  VALUES ('Chile');
  
  SET id_pais = LAST_INSERT_ID();
  
  INSERT INTO region (id_pais_FK, nombre)
  VALUES (id_pais, 'RM');
  
  SET id_region = LAST_INSERT_ID();
  
  INSERT INTO ciudad (id_region_FK, nombre)
  VALUES (id_region, 'Santiago');
  
  SET id_ciudad = LAST_INSERT_ID();

  INSERT INTO comuna (id_ciudad_FK, nombre)
  VALUES (id_ciudad, 'Santiago');
  INSERT INTO comuna (id_ciudad_FK, nombre)
  VALUES (id_ciudad, 'Cerrillos');
  INSERT INTO comuna (id_ciudad_FK, nombre)
  VALUES (id_ciudad, 'Cerro Navia');
  INSERT INTO comuna (id_ciudad_FK, nombre)
  VALUES (id_ciudad, 'Conchalí');
  INSERT INTO comuna (id_ciudad_FK, nombre)
  VALUES (id_ciudad, 'El Bosque');
  INSERT INTO comuna (id_ciudad_FK, nombre)
  VALUES (id_ciudad, 'Estación Central');
  INSERT INTO comuna (id_ciudad_FK, nombre)
  VALUES (id_ciudad, 'Huechuraba');
  INSERT INTO comuna (id_ciudad_FK, nombre)
  VALUES (id_ciudad, 'Independencia');
  INSERT INTO comuna (id_ciudad_FK, nombre)
  VALUES (id_ciudad, 'La Cisterna');
  INSERT INTO comuna (id_ciudad_FK, nombre)
  VALUES (id_ciudad, 'La Florida');
  INSERT INTO comuna (id_ciudad_FK, nombre)
  VALUES (id_ciudad, 'La Granja');
  INSERT INTO comuna (id_ciudad_FK, nombre)
  VALUES (id_ciudad, 'La Pintana');
  INSERT INTO comuna (id_ciudad_FK, nombre)
  VALUES (id_ciudad, 'La Reina');
  INSERT INTO comuna (id_ciudad_FK, nombre)
  VALUES (id_ciudad, 'Las Condes');
  INSERT INTO comuna (id_ciudad_FK, nombre)
  VALUES (id_ciudad, 'Lo Barnechea');
  INSERT INTO comuna (id_ciudad_FK, nombre)
  VALUES (id_ciudad, 'Lo Espejo');
  INSERT INTO comuna (id_ciudad_FK, nombre)
  VALUES (id_ciudad, 'Lo Prado');
  INSERT INTO comuna (id_ciudad_FK, nombre)
  VALUES (id_ciudad, 'Macul');
  INSERT INTO comuna (id_ciudad_FK, nombre)
  VALUES (id_ciudad, 'Maipú');
  INSERT INTO comuna (id_ciudad_FK, nombre)
  VALUES (id_ciudad, 'Ñuñoa');
  INSERT INTO comuna (id_ciudad_FK, nombre)
  VALUES (id_ciudad, 'Pedro Aguirre Cerda');
  INSERT INTO comuna (id_ciudad_FK, nombre)
  VALUES (id_ciudad, 'Peñalolén');
  INSERT INTO comuna (id_ciudad_FK, nombre)
  VALUES (id_ciudad, 'Providencia');
  INSERT INTO comuna (id_ciudad_FK, nombre)
  VALUES (id_ciudad, 'Pudahuel');
  INSERT INTO comuna (id_ciudad_FK, nombre)
  VALUES (id_ciudad, 'Quilicura');
  INSERT INTO comuna (id_ciudad_FK, nombre)
  VALUES (id_ciudad, 'Quinta Normal');
  INSERT INTO comuna (id_ciudad_FK, nombre)
  VALUES (id_ciudad, 'Recoleta');
  INSERT INTO comuna (id_ciudad_FK, nombre)
  VALUES (id_ciudad, 'Renca');
  INSERT INTO comuna (id_ciudad_FK, nombre)
  VALUES (id_ciudad, 'Joaquín');
  INSERT INTO comuna (id_ciudad_FK, nombre)
  VALUES (id_ciudad, 'San Miguel');
  INSERT INTO comuna (id_ciudad_FK, nombre)
  VALUES (id_ciudad, 'San Ramón');
  INSERT INTO comuna (id_ciudad_FK, nombre)
  VALUES (id_ciudad, 'Vitacura');
    
    
      
   -- COMMIT;
      
  
 END
  
  
$$
  
DELIMITER ;
