#!/bin/bash

if [ ! -d "./docker-compose/logs" ]; then
  echo "Creating logs folder..."
  mkdir logs
fi

LOGBACKEND="./docker-compose/logs/backend_build.prod.log"
rm $LOGBACKEND
LOGFRONTEND="./docker-compose/logs/frontend_build.prod.log"
rm $LOGFRONTEND

docker-compose -f docker-compose.prod.yml build wordle-backend >> $LOGBACKEND
echo "########################################### WORDLE BACKEND built ###########################################"

docker-compose -f docker-compose.prod.yml build wordle-frontend >> $LOGFRONTEND
echo "########################################### WORDLE FRONTEND built ###########################################"