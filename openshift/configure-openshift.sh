#!/bin/bash

# Adding Openshift remote
# http://stackoverflow.com/questions/12657168/can-i-use-my-existing-git-repo-with-openshift

# Set of commands to configure an app
rhc app-configure dev --deployment-branch master

# Command to deploy (HEAD could be any commit hash)
# git push openshift HEAD:master

DB_NAME=dev

mysql -h$OPENSHIFT_MYSQL_DB_HOST -u $OPENSHIFT_MYSQL_DB_USERNAME --password=$OPENSHIFT_MYSQL_DB_PASSWORD -e "DROP DATABASE IF EXISTS $DB_NAME"
mysql -h$OPENSHIFT_MYSQL_DB_HOST -u $OPENSHIFT_MYSQL_DB_USERNAME --password=$OPENSHIFT_MYSQL_DB_PASSWORD -e "CREATE DATABASE $DB_NAME"
mysql -h$OPENSHIFT_MYSQL_DB_HOST -u $OPENSHIFT_MYSQL_DB_USERNAME --password=$OPENSHIFT_MYSQL_DB_PASSWORD -e "GRANT ALL ON *.* TO '$OPENSHIFT_MYSQL_DB_USERNAME'@'%'"

mysql -h$OPENSHIFT_MYSQL_DB_HOST -u $OPENSHIFT_MYSQL_DB_USERNAME --password=$OPENSHIFT_MYSQL_DB_PASSWORD -e "UPDATE mysql.user SET Grant_priv='Y', Super_priv='Y' WHERE User='$OPENSHIFT_MYSQL_DB_PASSWORD'"
mysql -h$OPENSHIFT_MYSQL_DB_HOST -u $OPENSHIFT_MYSQL_DB_USERNAME --password=$OPENSHIFT_MYSQL_DB_PASSWORD
mysql -h$OPENSHIFT_MYSQL_DB_HOST -u $OPENSHIFT_MYSQL_DB_USERNAME --password=$OPENSHIFT_MYSQL_DB_PASSWORD $DB_NAME < graffitab_structure.sql