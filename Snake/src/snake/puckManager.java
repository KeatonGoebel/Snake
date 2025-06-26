package snake;

import java.util.Random;

public class puckManager {

    GamePanel gp;
    int spawnLocationX[];
    int spawnLocationY[];
    final int numOfSpawnLocations = 10;
    int currentSpawnLocation = 0; //

    public puckManager(GamePanel gp){

        this.gp = gp;

        // Initiating spawn locations
        spawnLocationX = new int[numOfSpawnLocations];
        spawnLocationY = new int[numOfSpawnLocations];

        spawnLocationX[0] = 500;
        spawnLocationY[0] = 400;

        spawnLocationX[1] = 200;
        spawnLocationY[1] = 200;

        spawnLocationX[2] = 252;
        spawnLocationY[2] = 252;

        spawnLocationX[3] = 150;
        spawnLocationY[3] = 400;

        spawnLocationX[4] = 500;
        spawnLocationY[4] = 150;

        spawnLocationX[5] = 600;
        spawnLocationY[5] = 200;

        spawnLocationX[6] = 200;
        spawnLocationY[6] = 420;

        spawnLocationX[7] = 100;
        spawnLocationY[7] = 300;

        spawnLocationX[8] = 500;
        spawnLocationY[8] = 152;

        spawnLocationX[9] = 500;
        spawnLocationY[9] = 300;
    }

    // things to do when puck is it:
    // move puck,
    // make snake longe
    public void updatePuck(){

        // Getting random number between 0 and 9
        Random random = new Random();
        int randomNumber = random.nextInt(numOfSpawnLocations);
        System.out.println("new spawn location is: " + randomNumber);

        // I want to make sure I don't ramdomly pick the same spawn location multiple times
        while (currentSpawnLocation == randomNumber){
            randomNumber = random.nextInt(numOfSpawnLocations);
        }

        // Moving puck
        gp.puckX = spawnLocationX[randomNumber];
        gp.puckY = spawnLocationY[randomNumber];
        currentSpawnLocation = randomNumber;
    }

}
