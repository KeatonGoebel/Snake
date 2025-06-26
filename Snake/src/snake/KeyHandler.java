package snake;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public boolean upPressed, leftPressed, rightPressed;
    public boolean downPressed = true;
    public boolean canChangeDirection = true;
    GamePanel gp;

    public KeyHandler(GamePanel gp){
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        int code = e.getKeyCode();

        // If canChangeDirection is false, then get out of function and block movement
        if(!canChangeDirection) {
            return;
        }

        if(code == KeyEvent.VK_W && !downPressed || code == KeyEvent.VK_UP && !downPressed){
            upPressed = true;
            leftPressed = false;
            downPressed = false;
            rightPressed = false;
            System.out.println("Now going Up!");
        }
        if(code == KeyEvent.VK_A && !rightPressed || code == KeyEvent.VK_LEFT && !rightPressed){
            leftPressed = true;
            upPressed = false;
            downPressed = false;
            rightPressed = false;
            System.out.println("Now going Left!");
        }
        if(code == KeyEvent.VK_S && !upPressed || code == KeyEvent.VK_DOWN && !upPressed){
            downPressed = true;
            leftPressed = false;
            upPressed = false;
            rightPressed = false;
            System.out.println("Now going Down!");
        }
        if(code == KeyEvent.VK_D && !leftPressed || code == KeyEvent.VK_RIGHT && !leftPressed){
            rightPressed = true;
            leftPressed = false;
            downPressed = false;
            upPressed= false;
            System.out.println("Now going Right!");
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
