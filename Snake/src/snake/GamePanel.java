package snake;

import javax.crypto.spec.PSource;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable{

    // Screen Settings
    final int originalTileSize = 16; // 16x16 tiles
    final int scale = 3;
    final int tileSize = originalTileSize * scale; // 48X48 tiles
    final int maxScreenCol = 16; // make sure col x row is at a 4x3 ratio
    final int maxScreenRow = 12;
    final int screenWidth  = tileSize * maxScreenCol; // 768 pixel width
    final int screenHeight = tileSize * maxScreenRow; // 576 pixel height
    final int minX = 0;
    final int maxX = screenWidth;
    final int minY = tileSize;
    final int maxY = screenHeight;

    // Score
    String scoreText = "Score: ";
    String score = "0";
    final int scoreXPos = 48;
    final int scoreYPos = 48;

    // High Score
    String highScoreText = "High Score: ";
    String highScore = "0";
    final int highScoreXPos = 192;
    final int highScoreYPos = 48;

    int FPS = 60;

    // Managers
    TileManager tileM = new TileManager(this);
    puckManager puckM = new puckManager(this);
    KeyHandler keyH = new KeyHandler(this);

    Thread gameThread;

    int START_GAME_X = 48;
    int START_GAME_Y = 96;

    // Set Player's default position
    int playerX = START_GAME_X;
    int playerY = START_GAME_Y;
    int playerSpeed = 4;

    // Set Pucks default position
    int puckX = 500;
    int puckY = 400;
    int puckSize = tileSize / 2;

    // Variables for movement of the snake
    ArrayList<Coordinate> locations = new ArrayList<Coordinate>();
    ArrayList<Coordinate> positionHistory = new ArrayList<>();
    final int MAX_SEGMENTS = 100;
    int movementProgress = 0;

    // bool is turned on when the player or puck interacts with collision tile
    boolean collisionOn;

    boolean gameover = false;
    JButton restartButton;

    public GamePanel() {

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
        this.requestFocusInWindow();

    }

    public void startGameThread() {

        if (gameThread == null || !gameThread.isAlive()) {
            gameThread = new Thread(this);
            locations.add(new Coordinate(playerX, playerY));
            gameThread.start();
        }

    }

    @Override
    public void run() {

        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        long drawCount = 0;

        while(gameThread != null){ // while gameThread exits, loop

            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

           if(!gameover) {

               if (delta >= 1) {
                   // 1 Update: information in game
                   // 2 Draw: the screen with updated information
                   update();
                   repaint();
                   delta--;
                   drawCount++;
               }
            }

            if(timer >= 1000000000){
                drawCount = 0;
                timer = 0;
            }
        }

    }

    public void update() {

        // Every second collisionOn is set to false, if player or puck is touching a
        // collision tile, then collisionOn will be set back to true
        collisionOn = false;
        collisionChecker();

            // Because our tile size is 48 and the speed is 4. That means the snake moved a full length every 12 frames
            // therefore we need to keep a record of every coordinate of the head and update the heads position every 12 frames

            positionHistory.add(0, new Coordinate(playerX,playerY));
            int maxHistorySize = (tileSize / playerSpeed) * MAX_SEGMENTS;
            if(positionHistory.size() > maxHistorySize){
                positionHistory.remove(positionHistory.size() - 1);
            }

            for(int i = 0; i < locations.size(); i ++){
                int index = (tileSize / playerSpeed) * (i + 1);
                if (index >= 0 && index < positionHistory.size()) {
                    Coordinate position = positionHistory.get(index);
                    if (i < locations.size()) {
                        locations.set(i, new Coordinate(position.getX(), position.getY()));
                    } else {
                        locations.add(new Coordinate(position.getX(), position.getY()));
                    }
                }
            }

        // Player can move if collisionOn is equal to false
        if(!collisionOn) {

            if(movementProgress < tileSize) {
                if (keyH.upPressed) {
                    playerY -= playerSpeed;
                }
                if (keyH.downPressed) {
                    playerY += playerSpeed;
                }
                if (keyH.leftPressed) {
                    playerX -= playerSpeed;
                }
                if (keyH.rightPressed) {
                    playerX += playerSpeed;
                }
                movementProgress += playerSpeed;
            } else {
                keyH.canChangeDirection = true;
                movementProgress = 0;
            }
        }

        // if Gameover is true, restart game and update high score
        if(gameover) {
            showRestartButton();
            if (Integer.parseInt(score) > Integer.parseInt(highScore)) {
                highScore = score;
            }
        }

    }
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g; // changing the Graphics parameter into a Graphics2D parameter, which has more functionality

        g2.setColor(Color.black);

        // By drawing the tiles first, we ensure the player is always on top
        tileM.draw(g2);

        // Score and High Scorewill be a Jlabel
        g2.setFont(new Font("Serif", Font.PLAIN, 30));
        g2.drawString(scoreText,scoreXPos,scoreYPos);
        g2.drawString(score, scoreXPos + 96, scoreYPos); // 96 equals tilesize * 2

        g2.setFont(new Font("Serif", Font.PLAIN, 30));
        g2.drawString(highScoreText,highScoreXPos,highScoreYPos);
        g2.drawString(highScore, highScoreXPos + 168, highScoreYPos); // 168 equals tilesize * 3.5

        // Create player
        g2.setColor(Color.white);
        for(int i = 0; i < locations.size(); i++){
            g2.fillRect(locations.get(i).getX(), locations.get(i).getY(), tileSize, tileSize);
        }

        g2.setColor(Color.red);
        // Create puck, which is half the size of player
        g2.fillRect(puckX,puckY,puckSize, puckSize);

        g2.dispose(); // dispose of the graphics and release any system resources it is using
    }

    public void collisionChecker() {

        // Finding position of sides of player and puck, easy because they are squares
        int leftSidePlayer = playerX;
        int rightSidePlayer = playerX + tileSize;
        int topSidePlayer = playerY;
        int botSidePlayer = playerY + tileSize;

        //int botSidePlayer = playerY + tileSize;
        int leftSidePuck = puckX;
        int rightSidePuck = puckX + puckSize;
        int topSidePuck = puckY;
        int botSidePuck = puckY + puckSize;

        if(keyH.upPressed) {

            // checking if I hit a wall
            if (topSidePlayer == minY) {
                collisionOn = true;
                System.out.println("Time to die! Hit top wall");
                gameover = true;
            }

            // checking if I hit the puck
            if(topSidePlayer == botSidePuck && rangeChecker(rightSidePlayer, leftSidePuck, rightSidePuck)
            || topSidePlayer == botSidePuck && rangeChecker(leftSidePlayer, leftSidePuck, rightSidePuck)
            || topSidePlayer == botSidePuck && rangeChecker(leftSidePlayer + (rightSidePlayer - leftSidePlayer) / 2, leftSidePuck, rightSidePuck)) {
                System.out.println("Player has hit the puck from the bot!");
                int currentScore = Integer.parseInt(score);
                currentScore += 1;
                score = String.valueOf(currentScore);
                locations.add(new Coordinate(playerX, playerY));
                puckM.updatePuck();
            }

            // checking if I hit myself
            for (int i = 1; i < locations.size(); i++){
                int botBody = locations.get(i).getY() + tileSize;
                int leftBody = locations.get(i).getX();
                int rightBody = locations.get(i).getX() + tileSize;
                //int topBody = locations.get(i).getY() + tileSize;
                if(topSidePlayer == botBody && rangeChecker(leftBody, leftSidePlayer, rightSidePlayer) ||
                    topSidePlayer == botBody && rangeChecker(rightBody, leftSidePlayer, rightSidePlayer)){
                    System.out.println("Time to die! hit myself from top" + " Hit Body num: " + i);
                    System.out.println("Player top: " + topSidePlayer + "Body bot: " + botBody);
                    System.out.println("Left Body: " + leftBody + "Body bot: " + botBody);
                    gameover = true;
                }
            }

        }
        if(keyH.downPressed) {

            //int tempBot = botSidePlayer - tileSize;
            if (botSidePlayer >= maxY) {
                collisionOn = true;
                System.out.println("Time to die! bot wall");
                gameover = true;
            }

            if(botSidePlayer == topSidePuck && rangeChecker(rightSidePlayer, leftSidePuck, rightSidePuck)
                    || botSidePlayer == topSidePuck && rangeChecker(leftSidePlayer, leftSidePuck, rightSidePuck)
                    || botSidePlayer == topSidePuck && rangeChecker(leftSidePlayer + (rightSidePlayer - leftSidePlayer) / 2, leftSidePuck, rightSidePuck)) {
                System.out.println("Player has hit the puck from the top!");
                int currentScore = Integer.parseInt(score);
                currentScore += 1;
                score = String.valueOf(currentScore);
                locations.add(new Coordinate(playerX, playerY));
                puckM.updatePuck();
            }

            for (int i = 1; i < locations.size(); i++){
                //int botBody = locations.get(i).getY() + tileSize;
                int leftBody = locations.get(i).getX();
                int rightBody = locations.get(i).getX() + tileSize;
                int topBody = locations.get(i).getY();
                if(botSidePlayer == topBody && rangeChecker(leftBody, leftSidePlayer, rightSidePlayer) ||
                    botSidePlayer == topBody && rangeChecker(rightBody, leftSidePlayer, rightSidePlayer)){
                    System.out.println("Time to Die! hit myself from bot" + " Hit Body num: " + i);
                    System.out.println("Bot top: " + botSidePlayer + "Body top: " + topBody);
                    gameover = true;
                }
            }
        }
        if(keyH.rightPressed) {

            if (rightSidePlayer == maxX) {
                collisionOn = true;
                System.out.println("Time to die! right wall");
                gameover = true;
            }

            if(rightSidePlayer == leftSidePuck && rangeChecker(topSidePlayer, topSidePuck, botSidePuck)
                    || rightSidePlayer == leftSidePuck && rangeChecker(botSidePlayer, topSidePuck, botSidePuck)
                    || rightSidePlayer == leftSidePuck && rangeChecker(topSidePlayer + (botSidePlayer - topSidePlayer) / 2, topSidePuck, botSidePuck)) {
                System.out.println("Player has hit the puck from the left!");
                int currentScore = Integer.parseInt(score);
                currentScore += 1;
                score = String.valueOf(currentScore);
                locations.add(new Coordinate(playerX, playerY));
                puckM.updatePuck();
            }

            for (int i = 1; i < locations.size(); i++){
                int botBody = locations.get(i).getY() + tileSize;
                int leftBody = locations.get(i).getX();
                //int rightBody = locations.get(i).getX() + tileSize;
                int topBody = locations.get(i).getY();
                if(rightSidePlayer == leftBody && rangeChecker(botBody, topSidePlayer, botSidePlayer) ||
                    rightSidePlayer == leftBody && rangeChecker(topBody, leftSidePlayer, rightSidePlayer)){
                    System.out.println("Time to Die! hit myself from right" + " Hit Body num: " + i);
                    System.out.println("Player right: " + rightSidePlayer + "Body left: " + leftBody);
                    gameover = true;
                }
            }
        }


        if(keyH.leftPressed) {

            if (leftSidePlayer == minX) {
                collisionOn = true;
                System.out.println("Time to die! left wall");
                gameover = true;
            }

            if(leftSidePlayer == rightSidePuck && rangeChecker(topSidePlayer, topSidePuck, botSidePuck)
                    || leftSidePlayer == rightSidePuck && rangeChecker(botSidePlayer, topSidePuck, botSidePuck)
                    || leftSidePlayer == rightSidePuck && rangeChecker(topSidePlayer + (botSidePlayer - topSidePlayer) / 2, topSidePuck, botSidePuck)) {
                System.out.println("Player has hit the puck from the right!");
                int currentScore = Integer.parseInt(score);
                currentScore += 1;
                score = String.valueOf(currentScore);
                locations.add(new Coordinate(playerX, playerY));
                puckM.updatePuck();
            }

            for (int i = 1; i < locations.size(); i++){
                int botBody = locations.get(i).getY() + tileSize;
                //int leftBody = locations.get(i).getX();
                int rightBody = locations.get(i).getX() + tileSize;
                int topBody = locations.get(i).getY();
                if(leftSidePlayer == rightBody && rangeChecker(botBody, topSidePlayer, botSidePlayer) ||
                    leftSidePlayer == rightBody && rangeChecker(topBody, leftSidePlayer, rightSidePlayer)){
                    System.out.println("Time to Die! hit myself from left" + " Hit Body num: " + i);
                    System.out.println("Player left: " + leftSidePlayer + "Body right: " + rightBody);
                    gameover = true;
                }
            }
        }
    }

    // This takes a value and rounds it to the nearest multiple of tilesize

    public int fixToTile(int value) {
        return Math.round((float)value / tileSize) * tileSize;
    }

    public boolean rangeChecker(int num, int lowerBound, int upperBound) {
        if(num >= lowerBound && num <= upperBound)
            return true;
        return false;
    }

    // Creating restart button when gameover is true
    public void showRestartButton() {
        JButton restartButton = new JButton("<html>You Lose <br> Restart?</html>");
        restartButton.setBounds(screenWidth / 2 - 100,screenHeight / 2 - 25,200,50);
        restartButton.setBackground(Color.red);
        restartButton.setForeground(Color.black);
        restartButton.setBorderPainted(false);
        restartButton.setOpaque(true);

        restartButton.addActionListener(e -> {
            resetGame();

            JLayeredPane layeredPane = (JLayeredPane) this.getParent();
            layeredPane.remove(restartButton);
            layeredPane.repaint();
        });

        JLayeredPane layeredPane = (JLayeredPane) this.getParent();
        layeredPane.add(restartButton, JLayeredPane.PALETTE_LAYER);
        layeredPane.repaint();
    }

    public void resetGame() {
        
        gameThread = null;
        gameover = false;
        movementProgress = 0;
        keyH.canChangeDirection = true;

        playerX = START_GAME_X;
        playerY = START_GAME_Y;
        score = "0";
        playerSpeed = 4;

        keyH.downPressed = true;
        keyH.upPressed = false;
        keyH.leftPressed = false;
        keyH.rightPressed = false;

        locations.clear();
        positionHistory.clear();

        startGameThread();
    }

}
