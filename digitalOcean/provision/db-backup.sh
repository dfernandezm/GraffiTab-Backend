#!/bin/bash

source /root/graffitab/environment.sh
DB_BACKUP_DEST=/root/graffitab/db-backups
DB_PASSWORD=$DO_MYSQL_DB_PASSWORD

mkdir -p $DB_BACKUP_DEST
mysqldump -u $DO_MYSQL_DB_USERNAME -p$DO_MYSQL_DB_PASSWORD graffitab > $DB_BACKUP_DEST/database_dump.sql