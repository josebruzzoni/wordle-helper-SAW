#!/bin/bash
cd docker-compose

LOGBACKEND="./logs/backend_build.log"
rm $LOGBACKEND
LOGFRONTEND="./logs/frontend_build.log"
rm $LOGFRONTEND

docker-compose build wordle-backend > $LOGBACKEND
echo "WORDLE BACKEND built"

docker-compose build wordle-frontend > $LOGFRONTEND
echo "WORDLE FRONTEND built"

cd ..