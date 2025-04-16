#!/bin/bash
# Use this script to reset a database with a SQL file.
# Requires that the database already exists to avoid creating orphaned db

if [ "$#" -ne 4 ]; then
    echo "Usage: $0 <db_name> <sql_file_path> <db_user> <db_password>"
    exit 1
fi

DB_NAME=$1
SQL_FILE_PATH=$2
DB_USER=$3
DB_PASSWORD=$4

SQL_DIR=$(dirname "$SQL_FILE_PATH")
SQL_FILE=$(basename "$SQL_FILE_PATH")

cd "$SQL_DIR" || { echo "Erreur: Impossible de changer de répertoire vers $SQL_DIR"; exit 1; }

mysql -u$DB_USER -p$DB_PASSWORD -e "DROP DATABASE \`$DB_NAME\`;"
if [ $? -ne 0 ]; then
    echo "Erreur: Échec de la suppression de la base de données $DB_NAME."
    exit 1
fi

mysql -u$DB_USER -p$DB_PASSWORD -e "CREATE DATABASE \`$DB_NAME\`;"
if [ $? -ne 0 ]; then
    echo "Erreur: Échec de la création de la base de données $DB_NAME."
    exit 1a
fi

mysql -u$DB_USER -p$DB_PASSWORD $DB_NAME < "$SQL_FILE"
if [ $? -ne 0 ]; then
    echo "Erreur: Échec de l'importation du fichier SQL $SQL_FILE."
    exit 1
fi

echo "La base de données $DB_NAME a été recréée avec succès à partir de $SQL_FILE_PATH"