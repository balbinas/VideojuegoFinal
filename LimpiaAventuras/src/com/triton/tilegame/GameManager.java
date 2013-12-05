package com.triton.tilegame;

import com.triton.Graphics.Sprite;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.*;

import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioFormat;
import com.triton.tilegame.sprites.Bullet;

import com.triton.Graphics.*;
import com.triton.sound.*;
import com.triton.input.*;
import com.triton.test.GameCore;
import static com.triton.test.GameCore.screen; //Aqui era el test antes
import com.triton.tilegame.sprites.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
    GameManager manages all parts of the game.
*/
public class GameManager extends GameCore implements Runnable, MouseListener, MouseMotionListener{

    public static void main(String[] args) {
        new GameManager().run();
    }

    // uncompressed, 44100Hz, 16-bit, mono, signed, little-endian
    private static final AudioFormat PLAYBACK_FORMAT =
        new AudioFormat(44100, 16, 1, true, false);

    private static final int DRUM_TRACK = 1;

    public static final float GRAVITY = 0.002f;

    private Point pointCache = new Point();
    private TileMap map;
    private MidiPlayer midiPlayer;
    private SoundManager soundManager;
    private ResourceManager resourceManager;
    private Sound prizeSound;
    private Sound boopSound;
    private InputManager inputManager;
    private TileMapRenderer renderer;

    private GameAction moveLeft;
    private GameAction moveRight;
    private GameAction jump;
    private GameAction exit;
    private GameAction shoot;
    private GameAction pause;
    private GameAction instructions;
    private GameAction credits;
    private GameAction sound;
    private GameAction restart;
    private int introCounter;
    public static ArrayList<Bullet> bullets;
    private int angle;
    private int bulletOffset;
    private Animation bulletAnim;
    
    public static int lives;
    public static int score;
   public static int levels;

    
    public static Image iIntro;
    public static Image iIntro2;
    public static Image iMenu;
    public static Image iInstr;
    public static Image iCredits;
    public static Image iBoy;
    public static Image iPause;
    public static Image iGameOver;
    public static Image iGirl;
    public static Image iLevel;
    public static Image iLoose;
    public static Image iWin;
    
    private boolean introff;
    private boolean menuoff;
    private boolean instroff;
    private boolean creditoff;
    //private boolean 
    private boolean pausoff;
    private boolean sonidoff;
    private boolean scoring;
    
        private boolean bIntro;
    private boolean bMenu;
    private boolean bInstr;
    private boolean bCredits;
    public static boolean bBoy;
    private boolean bGirl;
    private boolean bLevel;
    private boolean bLoose;
    private boolean bWin;
    private boolean bPlayer;
    
    private boolean bPause;
    private boolean bSound;


    public void init() {
        super.init();
        // set up input manager
        initInput();
        // start resource manager
        resourceManager = new ResourceManager(
        screen.getFullScreenWindow().getGraphicsConfiguration());
        // load resources
        renderer = new TileMapRenderer();
        renderer.setBackground(
            resourceManager.loadImage("background.png"));
        // load first map
        map = resourceManager.loadNextMap();
        bullets = new ArrayList<Bullet>();
        // load sounds
        soundManager = new SoundManager(PLAYBACK_FORMAT);
        prizeSound = soundManager.getSound("sounds/prize.wav");
        boopSound = soundManager.getSound("sounds/prize.wav");
        // start music
        midiPlayer = new MidiPlayer();
        Sequence sequence =
            midiPlayer.getSequence("sounds/roar.mid");
        midiPlayer.play(sequence, true);
        toggleDrumPlayback();
      
        lives = 1;
        score = 0;
                levels=0;

        iGameOver = ResourceManager.loadImage("screen_gameover_loose.png");
        introCounter = 350;
        iIntro = ResourceManager.loadImage("screen_intro.gif");
        iMenu = ResourceManager.loadImage("screen_menu.jpg");
        iPause = ResourceManager.loadImage("screen_instrucciones.png");
        iInstr = ResourceManager.loadImage("screen_instrucciones.png");
        iCredits = ResourceManager.loadImage("screen_creditos.png");
        //iBoy = ResourceManager.loadImage("chooseboy.png");
        //iGirl = ResourceManager.loadImage("choosegirl.png");
        //iLevel = ResourceManager.loadImage("levelcomplete.png");
        //iLoose = ResourceManager.loadImage("youloose.png");
        iWin = ResourceManager.loadImage("screen_gameover_win.png");
       
        
        introff = true;
        menuoff = false;
        instroff = false;
        creditoff = false;
        pausoff = false;
        sonidoff = true;
        
        bIntro = true;
        bMenu = false;
        bInstr = false;
        bCredits = false;
        bBoy = true;
        bGirl = false;
        bLevel = false;
        bLoose = false;
        bWin = false;
        bPause = false;
        bSound = true;
        bPlayer = false;
        
        //fileName = "scores.txt";
        //scorelist = new LinkedList<Integer>();
        scoring = false;
       
    }


    /**
        Closes any resources used by the GameManager.
    */
    public void stop() {
        super.stop();
        midiPlayer.close();
        soundManager.close();
    }


    private void initInput() {
        moveLeft = new GameAction("moveLeft");
        moveRight = new GameAction("moveRight");
        jump = new GameAction("jump",
            GameAction.DETECT_INITAL_PRESS_ONLY);
        exit = new GameAction("exit",
            GameAction.DETECT_INITAL_PRESS_ONLY);
        shoot = new GameAction("shoot", GameAction.DETECT_INITAL_PRESS_ONLY);
        pause = new GameAction("pause", GameAction.DETECT_INITAL_PRESS_ONLY);
        restart = new GameAction("restart", GameAction.DETECT_INITAL_PRESS_ONLY);
        credits = new GameAction("credits", GameAction.DETECT_INITAL_PRESS_ONLY);
        instructions = new GameAction("instructions", GameAction.DETECT_INITAL_PRESS_ONLY);
        sound = new GameAction("sound", GameAction.DETECT_INITAL_PRESS_ONLY);

        inputManager = new InputManager(
            screen.getFullScreenWindow());
        inputManager.setCursor(InputManager.INVISIBLE_CURSOR);

        inputManager.mapToKey(moveLeft, KeyEvent.VK_LEFT);
        inputManager.mapToKey(moveRight, KeyEvent.VK_RIGHT);
        inputManager.mapToKey(jump, KeyEvent.VK_UP);
        inputManager.mapToKey(exit, KeyEvent.VK_ESCAPE);
        inputManager.mapToKey(shoot, KeyEvent.VK_SPACE);
        inputManager.mapToKey(pause, KeyEvent.VK_P);
        inputManager.mapToKey(instructions, KeyEvent.VK_I);
                inputManager.mapToKey(restart, KeyEvent.VK_ENTER);
        inputManager.mapToKey(credits, KeyEvent.VK_C);
        inputManager.mapToKey(sound, KeyEvent.VK_S);
    }
    
        /**
     * Reinitializes variables, in order to restart the game.
     */
    private void restartGame() {
        
        resourceManager.setCurrentMap(1);
        map = resourceManager.reloadMap();
        
        lives = 3;
        score = 0;
                levels=0;
                
bWin=false;
        bPause = false;
        bSound = true;
        
       
        
    }


    private void checkInput(long elapsedTime) {

        if (exit.isPressed()) {
            stop();
        }

        Player player = (Player)map.getPlayer();
        if (player.isAlive()) {
            float velocityX = 0;
            if (moveLeft.isPressed()) {
                velocityX-=player.getMaxSpeed();
                
                angle=180;
                bulletOffset=0;
            }
            if (moveRight.isPressed()) {
                velocityX+=player.getMaxSpeed();
                
                angle=0;
                bulletOffset=player.getWidth()/2;
            }
            if (jump.isPressed()) {
                player.jump(true);
            }
           
            player.setVelocityX(velocityX);
            
            if(player.getStanding()==0){
                bulletAnim = ResourceManager.bulletAnimationLeft();
            }else{
                bulletAnim = ResourceManager.bulletAnimationRight();
            }
                                   
            if(shoot.isPressed() &&!bPause){
                
                       System.out.println(bullets.size());

                        bullets.add(new Bullet(bulletAnim, angle,
                                player.getX()+bulletOffset,
                                player.getY()+player.getHeight()/2-16) {});
                        
                        System.out.println(bullets.size());

                        map.addSprite(bullets.get(bullets.size()-1));
                        
                        
                        player.setBulletTimer(System.nanoTime());
            }
            
            
            
            if(pause.isPressed()){
                pausoff = !pausoff;
                 bPause = !bPause;

                midiPlayer.setPaused(pausoff);
            }
            
            if (lives<=0) {
                    //restartGame();
                }
            }
        
            if (restart.isPressed()) {
                if (bIntro) {
                    bMenu = true;
                    bIntro = false;
                }
                else if (bMenu) {
                    bMenu = false;
                    //restartGame();
                    bBoy = true;
                    bPlayer = true;
                }
                else if (bBoy && bPlayer) {
                    restartGame();
                    //map = resourceManager.reloadMap();
                    bPlayer=false;
                }
                else if (bGirl && bPlayer) {
                    restartGame();
                    //map = resourceManager.reloadMap();
                    bPlayer=false;
                }
                else if (lives<=0) {
                    restartGame();
                }
            }
            
            
            if (instructions.isPressed()) {
                bInstr = !bInstr;
            }
            
            if (credits.isPressed()) {
                bCredits = !bCredits;
            }
            
            
            if (sound.isPressed()) {
                sonidoff = !sonidoff;
                midiPlayer.setPaused(sonidoff);
            }
        }

    


    public void draw(Graphics2D g) {
        Window window = ScreenManager.device.getFullScreenWindow();
        Font font;
        try{
        font = ResourceManager.getFont();
        font = font.deriveFont(24f);
        g.setFont(font);
        }catch (FontFormatException ex) {
        } catch (IOException ex) {
        }
        

        if (bIntro && introCounter > 0){
            g.drawImage(iIntro, 0, 0,
                    window.getWidth(), window.getHeight(), null);
            introCounter--;
        }else if(bIntro){
            bIntro=false;
            bMenu=true;
            //printSimpleString(g,"Presiona ENTER Para Continuar",window.getWidth(),0,400);
        }else if (bMenu) {
            g.drawImage(iMenu, 0, 0,
                    window.getWidth(), window.getHeight(), null);
        }else if (lives>0  && !bWin) {
            if (!bPause){
                
            renderer.draw(g, map,
                screen.getWidth(), screen.getHeight());

        
               g.drawString("Puntaje: " + score, 5, 60);
            } else {
                g.drawImage(iPause, 0, 0,
                    window.getWidth(), window.getHeight(), null);
                }
        }else if(bWin){
           g.drawImage(iWin, 0, 0,
                    window.getWidth(), window.getHeight(), null);
                        g.drawString("Tu score fue : " + score, 250,  100);

        }else{
            g.drawImage(iGameOver, 0, 0,
                    window.getWidth(), window.getHeight(), null);
                                     g.drawString("Tu score fue : " + score, 250, 100);

        }
        
        if (bInstr) {
            g.drawImage(iInstr, 0, 0,
                    window.getWidth(), window.getHeight(), null);
        }
        else if (bCredits) {
            g.drawImage(iCredits, 0, 0,
                    window.getWidth(), window.getHeight(), null);
     
         }
    }  
         
    


    /**
        Gets the current map.
    */
    public TileMap getMap() {
        return map;
    }


    /**
        Turns on/off drum playback in the midi music (track 1).
    */
    public void toggleDrumPlayback() {
        Sequencer sequencer = midiPlayer.getSequencer();
        if (sequencer != null) {
            sequencer.setTrackMute(DRUM_TRACK,
                !sequencer.getTrackMute(DRUM_TRACK));
        }
    }


    /**
        Gets the tile that a Sprites collides with. Only the
        Sprite's X or Y should be changed, not both. Returns null
        if no collision is detected.
    */
    public Point getTileCollision(Sprite sprite,
        float newX, float newY)
    {
        float fromX = Math.min(sprite.getX(), newX);
        float fromY = Math.min(sprite.getY(), newY);
        float toX = Math.max(sprite.getX(), newX);
        float toY = Math.max(sprite.getY(), newY);

        // get the tile locations
        int fromTileX = TileMapRenderer.pixelsToTiles(fromX);
        int fromTileY = TileMapRenderer.pixelsToTiles(fromY);
        int toTileX = TileMapRenderer.pixelsToTiles(
            toX + sprite.getWidth() - 1);
        int toTileY = TileMapRenderer.pixelsToTiles(
            toY + sprite.getHeight() - 1);

        // check each tile for a collision
        for (int x=fromTileX; x<=toTileX; x++) {
            for (int y=fromTileY; y<=toTileY; y++) {
                if (x < 0 || x >= map.getWidth() ||
                    map.getTile(x, y) != null)
                {
                    // collision found, return the tile
                    pointCache.setLocation(x, y);
                    return pointCache;
                }
            }
        }

        // no collision found
        return null;
    }


    /**
        Checks if two Sprites collide with one another. Returns
        false if the two Sprites are the same. Returns false if
        one of the Sprites is a Creature that is not alive.
    */
    public boolean isCollision(Sprite s1, Sprite s2) {
        // if the Sprites are the same, return false
        if (s1 == s2) {
            return false;
        }

        // if one of the Sprites is a dead Creature, return false
        if (s1 instanceof Creature && !((Creature)s1).isAlive()) {
            return false;
        }
        if (s2 instanceof Creature && !((Creature)s2).isAlive()) {
            return false;
        }

        // get the pixel location of the Sprites
        int s1x = Math.round(s1.getX());
        int s1y = Math.round(s1.getY());
        int s2x = Math.round(s2.getX());
        int s2y = Math.round(s2.getY());

        // check if the two sprites' boundaries intersect
        return (s1x < s2x + s2.getWidth() &&
            s2x < s1x + s1.getWidth() &&
            s1y < s2y + s2.getHeight() &&
            s2y < s1y + s1.getHeight());
    }


    /**
        Gets the Sprite that collides with the specified Sprite,
        or null if no Sprite collides with the specified Sprite.
    */
    public Sprite getSpriteCollision(Sprite sprite) {

        // run through the list of Sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite otherSprite = (Sprite)i.next();
            if (isCollision(sprite, otherSprite)) {
                // collision found, return the Sprite
                return otherSprite;
            }
        }

        // no collision found
        return null;
    }


    /**
        Updates Animation, position, and velocity of all Sprites
        in the current map.
    */
    public void update(long elapsedTime) {
        Creature player = (Creature)map.getPlayer();

        // player is dead! start map over
        if (player.getState() == Creature.STATE_DEAD) {
            map = resourceManager.reloadMap();
            return;
        }

        // get keyboard/mouse input
        checkInput(elapsedTime);

        // update player
        updateCreature(player, elapsedTime);
        player.update(elapsedTime);
        

        // update other sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite sprite = (Sprite)i.next();
            if (sprite instanceof Creature) {
                Creature creature = (Creature)sprite;
                if (creature.getState() == Creature.STATE_DEAD) {
                    i.remove();
                }
                else {
                    updateCreature(creature, elapsedTime);
                }
            }
            // normal update
            sprite.update(elapsedTime);
        }
        
        for(int j = 0; j < bullets.size(); j++){
                boolean remove = bullets.get(j).updateBullet(elapsedTime);
                //if(remove){
                    //map.removeSprite(bullets.get(j));
                    //bullets.remove(j);
                  //  j--;
                //} 
            }
                    checkBulletCollision();

    }


    /**
        Updates the creature, applying gravity for creatures that
        aren't flying, and checks collisions.
    */
    private void updateCreature(Creature creature,
        long elapsedTime)
    {

        // apply gravity
        if (!creature.isFlying()) {
            creature.setVelocityY(creature.getVelocityY() +
                GRAVITY * elapsedTime);
        }

        // change x
        float dx = creature.getVelocityX();
        float oldX = creature.getX();
        float newX = oldX + dx * elapsedTime;
        Point tile =
            getTileCollision(creature, newX, creature.getY());
        if (tile == null) {
            creature.setX(newX);
        }
        else {
            // line up with the tile boundary
            if (dx > 0) {
                creature.setX(
                    TileMapRenderer.tilesToPixels(tile.x) -
                    creature.getWidth());
            }
            else if (dx < 0) {
                creature.setX(
                    TileMapRenderer.tilesToPixels(tile.x + 1));
            }
            creature.collideHorizontal();
        }
        if (creature instanceof Player) {
            checkPlayerCollision((Player)creature, false);
        }

        // change y
        float dy = creature.getVelocityY();
        float oldY = creature.getY();
        float newY = oldY + dy * elapsedTime;
        tile = getTileCollision(creature, creature.getX(), newY);
        if (tile == null) {
            creature.setY(newY);
        }
        else {
            // line up with the tile boundary
            if (dy > 0) {
                creature.setY(
                    TileMapRenderer.tilesToPixels(tile.y) -
                    creature.getHeight());
            }
            else if (dy < 0) {
                creature.setY(
                    TileMapRenderer.tilesToPixels(tile.y + 1));
            }
            creature.collideVertical();
        }
        if (creature instanceof Player) {
            boolean canKill = (oldY < creature.getY());
            checkPlayerCollision((Player)creature, canKill);
        }

    }


    /**
        Checks for Player collision with other Sprites. If
        canKill is true, collisions with Creatures will kill
        them.
    */
    public void checkPlayerCollision(Player player,
        boolean canKill)
    {
        if (!player.isAlive()) {
            return;
        }

        // check for player collision with other sprites
        Sprite collisionSprite = getSpriteCollision(player);
        if (collisionSprite instanceof PowerUp) {
            acquirePowerUp((PowerUp)collisionSprite);
        }
        else if (collisionSprite instanceof Creature) {
            Creature badguy = (Creature)collisionSprite;
            if (canKill) {
                // kill the badguy and make player bounce
                soundManager.play(boopSound);
                badguy.setState(Creature.STATE_DYING);
                player.setY(badguy.getY() - player.getHeight());
                player.jump(true);
                
                score+=100;
            }
            else {
                // player dies!
                lives--;
                player.setState(Creature.STATE_DYING);
            }
        }
        else if (collisionSprite instanceof Shot) {
            Shot bala = (Shot)collisionSprite;
           // bala.setState(bala.STATE_NORMAL);
            
            player.setState(Creature.STATE_NORMAL);
        }
    }

         /**
     * Checks for Grub collision with bullets. Bullets kill grub.
     * 
     * @param grub Grub
     * @param bullet Bullet
     */
    public void checkBulletCollision()
    {
        for(int i = 0; i<bullets.size(); i++){
            Sprite collisionSprite = getSpriteCollision(bullets.get(i));
            if(collisionSprite instanceof Creature || collisionSprite instanceof Fly){
                Creature badguy = (Creature)collisionSprite;
                map.removeSprite(badguy);
                map.removeSprite(bullets.get(i));
                bullets.remove(i);
                score+=100;
            }
        }
    }

    /**
        Gives the player the speicifed power up and removes it
        from the map.
    */
    public void acquirePowerUp(PowerUp powerUp) {
        // remove it from the map
        map.removeSprite(powerUp);

        //Paste de Dientes
        if (powerUp instanceof PowerUp.Star) {
            // do something here, like give the player points
            soundManager.play(prizeSound);
            score+=100;
        }
        //Shampoo
        else if (powerUp instanceof PowerUp.Music) {
            // change the music
            soundManager.play(prizeSound);
            toggleDrumPlayback();
           score+=100;
        }
        //Goal
        else if (powerUp instanceof PowerUp.Goal) {
            // advance to next map
            levels++;
            if(levels>=2)
                bWin = true;
            soundManager.play(prizeSound,
                new EchoFilter(2000, .7f), false);
            map = resourceManager.loadNextMap();
            score+=500;
        }
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mousePressed(MouseEvent me) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent me) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
