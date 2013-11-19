/usr/bin/mysql -u root -p --execute="drop database amigo_seguro;"
/usr/bin/mysql -u root -p --default-character-set=latin1 < /home/lfhernandez/java/ASDaemon/bd/scripts/create.sql
/usr/bin/mysql -u root -p --default-character-set=latin1 < /home/lfhernandez/java/ASDaemon/bd/scripts/users.sql
/usr/bin/mysql -u root -p --default-character-set=latin1 -D amigo_seguro < /home/lfhernandez/java/ASDaemon/bd/scripts/sp.sql
/usr/bin/mysql -u root -p --default-character-set=latin1 -D amigo_seguro --execute="call carga_datos();"
