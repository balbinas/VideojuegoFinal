package com.triton.tilegame;

import com.triton.tilegame.sprites.Grub;
import com.triton.tilegame.sprites.Fly;
import com.triton.tilegame.sprites.Player;
import com.triton.tilegame.sprites.PowerUp;
import com.triton.tilegame.sprites.Shot;
import com.triton.Graphics.Sprite;
import com.triton.Graphics.Animation;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;

import com.triton.Graphics.*;


/**
    The ResourceManager class loads and manages tile Images and
    "host" Sprites used in the game. Game Sprites are cloned from
    "host" Sprites.
*/
public class ResourceManager {

    private ArrayList tiles;
    private int currentMap;
    private static GraphicsConfiguration gc;

    // host sprites used for cloning
    public Sprite bulletSprite;
    private Sprite playerSprite;
    private Sprite musicSprite;
    private Sprite coinSprite;
    private Sprite goalSprite;
    private Sprite grubSprite;
    private Sprite flySprite;

    /**
        Creates a new ResourceManager with the specified
        GraphicsConfiguration.
    */
    public ResourceManager(GraphicsConfiguration gc) {
        this.gc = gc;
        loadTileImages();
        loadCreatureSprites();
        loadPowerUpSprites();
    }


    /**
        Gets an image from the images/ directory.
    */
    public static Image loadImage(String name) {
        String filename = "images/" + name;
        return new ImageIcon(filename).getImage();
    }


    public static Image getMirrorImage(Image image) {
        return getScaledImage(image, -1, 1);
    }


    public Image getFlippedImage(Image image) {
        return getScaledImage(image, 1, -1);
    }


    private static Image getScaledImage(Image image, float x, float y) {

        // set up the transform
        AffineTransform transform = new AffineTransform();
        transform.scale(x, y);
        transform.translate(
            (x-1) * image.getWidth(null) / 2,
            (y-1) * image.getHeight(null) / 2);

        // create a transparent (not translucent) image
        Image newImage = gc.createCompatibleImage(
            image.getWidth(null),
            image.getHeight(null),
            Transparency.BITMASK);

        // draw the transformed image
        Graphics2D g = (Graphics2D)newImage.getGraphics();
        g.drawImage(image, transform, null);
        g.dispose();

        return newImage;
    }


    public TileMap loadNextMap() {
        TileMap map = null;
        while (map == null) {
            currentMap++;
            try {
                map = loadMap(
                    "maps/map" + currentMap + ".txt");
            }
            catch (IOException ex) {
                if (currentMap == 1) {
                    // no maps to load!
                    return null;
                }
                currentMap = 0;
                map = null;
            }
        }

        return map;
    }


    public TileMap reloadMap() {
        try {
            return loadMap(
                "maps/map" + currentMap + ".txt");
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    private TileMap loadMap(String filename)
        throws IOException
    {
        ArrayList lines = new ArrayList();
        int width = 0;
        int height = 0;

        // read every line in the text file into the list
        BufferedReader reader = new BufferedReader(
            new FileReader(filename));
        while (true) {
            String line = reader.readLine();
            // no more lines to read
            if (line == null) {
                reader.close();
                break;
            }

            // add every line except for comments
            if (!line.startsWith("#")) {
                lines.add(line);
                width = Math.max(width, line.length());
            }
        }

        // parse the lines to create a TileEngine
        height = lines.size();
        TileMap newMap = new TileMap(width, height);
        for (int y=0; y<height; y++) {
            String line = (String)lines.get(y);
            for (int x=0; x<line.length(); x++) {
                char ch = line.charAt(x);

                // check if the char represents tile A, B, C etc.
                int tile = ch - 'A';
                if (tile >= 0 && tile < tiles.size()) {
                    newMap.setTile(x, y, (Image)tiles.get(tile));
                }

                // check if the char represents a sprite
                else if (ch == 'o') {
                    addSprite(newMap, coinSprite, x, y);
                }
                else if (ch == '!') {
                    addSprite(newMap, musicSprite, x, y);
                }
                else if (ch == '*') {
                    addSprite(newMap, goalSprite, x, y);
                }
                else if (ch == '1') {
                    addSprite(newMap, grubSprite, x, y);
                }
                else if (ch == '2') {
                    addSprite(newMap, flySprite, x, y);
                }
            }
        }

        // add the player to the map
        Sprite player = (Sprite)playerSprite.clone();
        player.setX(TileMapRenderer.tilesToPixels(3));
        player.setY(0);
        newMap.setPlayer(player);

        return newMap;
    }


    private void addSprite(TileMap map,
        Sprite hostSprite, int tileX, int tileY)
    {
        if (hostSprite != null) {
            // clone the sprite from the "host"
            Sprite sprite = (Sprite)hostSprite.clone();

            // center the sprite
            sprite.setX(
                TileMapRenderer.tilesToPixels(tileX) +
                (TileMapRenderer.tilesToPixels(1) -
                sprite.getWidth()) / 2);

            // bottom-justify the sprite
            sprite.setY(
                TileMapRenderer.tilesToPixels(tileY + 1) -
                sprite.getHeight());

            // add it to the map
            map.addSprite(sprite);
        }
    }
    
        public void addBullet(TileMap map, int tileX, int tileY)
    {
            
            // clone the sprite from the "host"
            Sprite sprite = (Sprite)bulletSprite.clone();

            // center the sprite
            sprite.setX(tileX + 80);

            // bottom-justify the sprite
            sprite.setY(tileY + 20);

            // add it to the map
            map.addSprite(sprite);
    }



    // -----------------------------------------------------------
    // code for loading sprites and images
    // -----------------------------------------------------------


    public void loadTileImages() {
        // keep looking for tile A,B,C, etc. this makes it
        // easy to drop new tiles in the images/ directory
        tiles = new ArrayList();
        char ch = 'A';
        while (true) {
            String name = "tile_" + ch + ".png";
            File file = new File("images/" + name);
            if (!file.exists()) {
                System.out.println(name + " no existio");
                break;
            }
            tiles.add(loadImage(name));
            ch++;
        }
    }


    public void loadCreatureSprites() {

        Image[][] images = new Image[4][];

        // load left-facing images
        images[0] = new Image[] {
            loadImage("player1.png"),
            loadImage("player2.png"),
            loadImage("player3.png"),
            loadImage("fly1.png"),
            loadImage("fly2.png"),
            loadImage("fly3.png"),
            loadImage("sucio_01.png"),
            loadImage("sucio_02.png"),
            loadImage("sucio_03.png"),
            loadImage("powerup1.png"),
            loadImage("powerup2.png"),
            loadImage("powerup3.png")
            
        };

        // right-facing images
        images[1] = new Image[images[0].length];
        // left-facing "dead" images
        images[2] = new Image[images[0].length];
        // right-facing "dead" images
        images[3] = new Image[images[0].length];
        
        for (int i=0; i<images[0].length; i++) {
            // right-facing images
            images[1][i] = getMirrorImage(images[0][i]);
            // left-facing "dead" images
            images[2][i] = getFlippedImage(images[0][i]);
            // right-facing "dead" images
            images[3][i] = getFlippedImage(images[1][i]);
        }

        // create creature animations
        Animation[] playerAnim = new Animation[4];
        Animation[] flyAnim = new Animation[4];
        Animation[] grubAnim = new Animation[4];
        Animation[] bulletAnim = new Animation[4];
        
        for (int i=0; i<4; i++) {
            playerAnim[i] = createPlayerAnim(
                images[i][0], images[i][1], images[i][2]);
            flyAnim[i] = createFlyAnim(
                images[i][3], images[i][4], images[i][5]);
            grubAnim[i] = createGrubAnim(
                images[i][6], images[i][7], images[i][8]);
            bulletAnim[i] = createBulletAnim(images[i][9],images[i][10],images[i][11]);
        }

        // create creature sprites
        playerSprite = new Player(playerAnim[0], playerAnim[1],
            playerAnim[2], playerAnim[3]);
        flySprite = new Fly(flyAnim[0], flyAnim[1],
            flyAnim[2], flyAnim[3]);
        grubSprite = new Grub(grubAnim[0], grubAnim[1],
            grubAnim[2], grubAnim[3]);
        //bulletSprite = new Shot(bulletAnim[0],bulletAnim[1],
        //bulletAnim[2],bulletAnim[3]);
        
        
    }


    private Animation createBulletAnim(Image bullet1, Image bullet2, Image bullet3)
    {
        Animation anim = new Animation();
        anim.addFrame(bullet1, 50);
        anim.addFrame(bullet2, 50);
        anim.addFrame(bullet3, 50);
        return anim;
    }
    
      public static Animation bulletAnimationLeft(){
        Animation anim = new Animation();
        
        anim.addFrame(loadImage("bullet1.png"), 50);
        anim.addFrame(loadImage("bullet2.png"), 50);
        anim.addFrame(loadImage("bullet3.png"), 50);
        
        return anim;
    }
    public static Animation bulletAnimationRight(){
        Animation anim = new Animation();
        
        anim.addFrame(getMirrorImage(loadImage("bullet1.png")), 50);
        anim.addFrame(getMirrorImage(loadImage("bullet2.png")), 50);
        anim.addFrame(getMirrorImage(loadImage("bullet3.png")), 50);
        
        return anim;
    }
    
    private Animation createPlayerAnim(Image player1,
        Image player2, Image player3)
    {
        Animation anim = new Animation();
        anim.addFrame(player1, 250);
        anim.addFrame(player2, 150);
        anim.addFrame(player1, 150);
        anim.addFrame(player2, 150);
        anim.addFrame(player3, 200);
        anim.addFrame(player2, 150);
        return anim;
    }


    private Animation createFlyAnim(Image img1, Image img2,
        Image img3)
    {
        Animation anim = new Animation();
        anim.addFrame(img1, 50);
        anim.addFrame(img2, 50);
        anim.addFrame(img3, 50);
        anim.addFrame(img2, 50);
        return anim;
    }


    private Animation createGrubAnim(Image img1, Image img2, Image img3) {
        Animation anim = new Animation();
        anim.addFrame(img1, 250);
        anim.addFrame(img2, 250);
        anim.addFrame(img3,250);
        return anim;
    }


    private void loadPowerUpSprites() {
        // create "goal" sprite
        Animation anim = new Animation();
        anim.addFrame(loadImage("Items-02.png"), 150);
        goalSprite = new PowerUp.Goal(anim);

        // create "pasta de dientes" sprite
        anim = new Animation();
        anim.addFrame(loadImage("Items-01.png"), 100);
        coinSprite = new PowerUp.Star(anim);

        // create "soap" sprite
        anim = new Animation();
        anim.addFrame(loadImage("Items-03.png"), 150);
        musicSprite = new PowerUp.Music(anim);
    }

}
