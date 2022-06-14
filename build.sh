#!/bin/bash
cd docker-compose

if [ ! -d "./logs" ]; then
  echo "Creating logs folder..."
  mkdir logs
fi

LOGMONGODB="./logs/mongodb_build.log"
rm $LOGMONGODB
LOGBACKEND="./logs/backend_build.log"
rm $LOGBACKEND
LOGFRONTEND="./logs/frontend_build.log"
rm $LOGFRONTEND

docker-compose build mongo >> $LOGMONGODB
echo "########################################### MONGODB built ###########################################"

docker-compose build wordle-backend >> $LOGBACKEND
echo "########################################### WORDLE BACKEND built ###########################################"

docker-compose build wordle-frontend >> $LOGFRONTEND
echo "########################################### WORDLE FRONTEND built ###########################################"

cd ..