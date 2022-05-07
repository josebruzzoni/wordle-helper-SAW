#!/bin/bash
git checkout main
git pull
echo "########################################### WORDLE BACKEND UPDATED ###########################################"

cd ../wordle-helper-front
git checkout main
git pull
echo "########################################### WORDLE FRONT UPDATED ###########################################"

cd ../wordle-helper