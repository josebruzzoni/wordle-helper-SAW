#!/bin/bash

if [ ! -d "./docker-compose/logs" ]; then
  echo "Creating logs folder..."
  mkdir logs
fi

LOGMONGODB="./docker-compose/logs/mongodb_build.log"
rm $LOGMONGODB
LOGBACKEND="./docker-compose/logs/backend_build.log"
rm $LOGBACKEND
LOGFRONTEND="./docker-compose/logs/frontend_build.log"
rm $LOGFRONTEND

docker-compose build mongo >> $LOGMONGODB
echo "########################################### MONGODB built ###########################################"

docker-compose build wordle-backend >> $LOGBACKEND
echo "########################################### WORDLE BACKEND built ###########################################"

docker-compose build wordle-frontend >> $LOGFRONTEND
echo "########################################### WORDLE FRONTEND built ###########################################"