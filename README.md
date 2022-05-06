# Wordle Helper App
A Wordle helper application design to help users organize tournaments and compare their scores with other players.

## How to Build Locally
1. Make sure the **Frontend (wordle-helper-front)** and the **Backend (wordle-helper)** are placed at the same level folder
2. Go to the root of the **wordle-helper** project
3. Open a terminal and run ``. pull.sh``. This will pull from main the latest changes in both repositories.
4. Run ``. build.sh``. This will build the docker images. The build logs are stored in *wordle-helper/docker-compose/logs* folder.

## How to Run services
5. Once built, at the root of the **wordle-helper** project run ``. start.sh``. This will start the services' containers.
6. Open [http://localhost:3000](http://localhost:3000) to view it in your browser.

## How to Stop services
1. At the root of the **wordle-helper** project run ``. stop.sh``. This will stop the services' containers.


## API documentation
https://app.swaggerhub.com/apis/TACS-WordleHelper/Wordle-Helper/1.0.4

## Frontend
https://github.com/JulianSima/wordle-helper-front
