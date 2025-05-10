#!/bin/bash
# Database backup script

DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/backups"
DB_NAME="social_commerce"

echo "Starting database backup..."
pg_dump -U postgres -h localhost $DB_NAME > $BACKUP_DIR/backup_$DATE.sql
echo "Backup completed: $BACKUP_DIR/backup_$DATE.sql"
