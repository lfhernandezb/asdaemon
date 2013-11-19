CREATE USER 'asdaemon'@'localhost' IDENTIFIED BY 'asdaemon';

GRANT SELECT, INSERT, UPDATE, DELETE ON amigo_seguro.* TO 'asdaemon'@'localhost';

GRANT CREATE, DROP ON amigo_seguro.* TO 'asdaemon'@'localhost';

GRANT EXECUTE ON amigo_seguro.* TO 'asdaemon'@'localhost';

CREATE USER 'batchas'@'localhost' IDENTIFIED BY 'batchas';

grant SELECT on TABLE `amigo_seguro`.`umbral_usuario` to 'batchas'@'localhost';
grant DELETE on TABLE `amigo_seguro`.`notificacion` to 'batchas'@'localhost';

