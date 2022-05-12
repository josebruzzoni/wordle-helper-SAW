#!/bin/bash
cd docker-compose

if [ ! -d "./logs" ]; then
  echo "Creating logs folder..."
  mkdir logs
fi

LOG="./logs/start.log"

rm $LOG
echo "restart.sh started" > $LOG
date >> $LOG
docker-compose down >> $LOG
echo "services stopped" >> $LOG
docker-compose up -d >> $LOG
echo "########################################### WORDLE services started ###########################################" >> $LOG
cd ..
docker ps
