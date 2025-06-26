package snake;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TileManager {

    GamePanel gp;
    Tile[] tile;
    int mapTileNum [] [];
    final int numOfTiles = 3;

    public TileManager(GamePanel gp){

        this.gp = gp;

        tile = new Tile[numOfTiles];
        mapTileNum = new int[gp.maxScreenCol] [gp.maxScreenRow];

        getTileImage();
        loadMap("map.txt");
    }

    public void getTileImage(){

        try {

            tile[0] = new Tile();
            tile[0].image = ImageIO.read(getClass().getResourceAsStream("tiles/earth.png"));
            tile[0].collision = true;

            tile[1] = new Tile();
            tile[1].image = ImageIO.read(getClass().getResourceAsStream("tiles/grass0.png"));

            tile[2] = new Tile();
            tile[2].image = ImageIO.read(getClass().getResourceAsStream("tiles/grass1.png"));


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void loadMap(String fileName) {

        try {
            InputStream is = getClass().getResourceAsStream(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int col = 0;
            int row = 0;

            while(col < gp.maxScreenCol && row < gp.maxScreenRow){

                String line = br.readLine();

                while(col < gp.maxScreenCol) {

                    String numbers[] = line.split(" ");

                    int num = Integer.parseInt(numbers[col]);

                    mapTileNum[col][row] = num;
                    col ++;
                }
                if(col == gp.maxScreenCol) {
                    col = 0;
                    row ++;
                }
            }
            br.close();

        }catch(Exception e){

        }
    }
    public void draw(Graphics g2) {

       int col = 0;
       int row = 0;
       int x = 0;
       int y = 0;

       while(col < gp.maxScreenCol && row < gp.maxScreenRow){

           int tileNum = mapTileNum[col][row];

           g2.drawImage(tile[tileNum].image, x, y, gp.tileSize, gp.tileSize, null);
           col ++;
           x += gp.tileSize;

           if(col == gp.maxScreenCol){
               col = 0;
               x = 0;
               row ++;
               y += gp.tileSize;
           }
       }
    }
}
