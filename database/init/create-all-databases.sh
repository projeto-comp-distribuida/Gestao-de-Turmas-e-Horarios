#!/bin/bash
# Script para criar todos os bancos de dados necessários para os microserviços
# Uso: ./database/init/create-all-databases.sh

set -e

POSTGRES_CONTAINER="${POSTGRES_CONTAINER:-postgres-schedules}"
POSTGRES_USER="${POSTGRES_USER:-distrischool}"

echo "Creating DistriSchool databases..."

# Lista de bancos de dados a criar
DATABASES=(
    "distrischool_schedules"
    "distrischool_auth"
    "distrischool_students"
    "distrischool_teachers"
)

for db in "${DATABASES[@]}"; do
    echo "Checking/Creating database: $db"
    docker exec -i "$POSTGRES_CONTAINER" psql -U "$POSTGRES_USER" -d postgres -tc \
        "SELECT 1 FROM pg_database WHERE datname = '$db'" | grep -q 1 || \
    docker exec -i "$POSTGRES_CONTAINER" psql -U "$POSTGRES_USER" -d postgres -c \
        "CREATE DATABASE $db;" && echo "  ✓ Database $db created" || echo "  ✓ Database $db already exists"
done

echo "All databases are ready!"



