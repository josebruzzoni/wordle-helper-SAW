#!/bin/bash

if [ ! -d "./docker-compose/logs" ]; then
  echo "Creating logs folder..."
  mkdir logs
fi

LOG="./docker-compose/logs/start.prod.log"

rm $LOG
echo "restart.sh started" > $LOG
date >> $LOG
docker-compose -f docker-compose.prod.yml down >> $LOG
echo "services stopped" >> $LOG
docker-compose -f docker-compose.prod.yml up -d >> $LOG
echo "########################################### WORDLE services started ###########################################" >> $LOG

docker ps
