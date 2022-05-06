#!/bin/bash
cd docker-compose

LOG="./logs/start.log"

rm $LOG
echo "restart.sh started" > $LOG
date >> $LOG
docker-compose down >> $LOG
echo "services stopped" >> $LOG
docker-compose up -d >> $LOG
echo "WORDLE services started" >> $LOG
cd ..
docker ps
