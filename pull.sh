#!/bin/bash
git checkout main
git pull
echo "*** BACKEND UPDATED ***"

cd ../wordle-helper-front
git checkout main
git pull
echo "*** FRONT UPDATED ***"

cd ../wordle-helper