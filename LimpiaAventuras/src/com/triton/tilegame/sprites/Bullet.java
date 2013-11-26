package com.triton.tilegame.sprites;

import com.triton.Graphics.Sprite;
import com.triton.tilegame.GameManager;
import com.triton.Graphics.Animation;
import com.triton.tilegame.TileMapRenderer;
import java.lang.reflect.Constructor;
import com.triton.Graphics.*;
import java.awt.Color;
import java.awt.Graphics2D;

/**
    A PowerUp class is a Sprite that the player can pick up.
*/
public abstract class Bullet extends Sprite {

    private int r;
    
    private double ddx;
    private double ddy;
    private double rad;
    private double speed;
    private boolean live;
    
    private Color color1;
    
    //Constructor
    public Bullet(Animation anim, double angle, float x, float y){
        
        super(anim, x, y);
        
        r=6;
        
        rad = Math.toRadians(angle);
        speed = 1;
        ddx = Math.cos(rad) * speed;
        ddy = Math.sin(rad) * speed;
        
        color1 = Color.BLUE;
        live=false;
        
    }
    
    public boolean updateBullet(long elapsedTime){
        this.setX(this.getX()+(float)ddx);
        this.setY(this.getY()+(float)ddy);
        
        //anim.update(elapsedTime);
        
        if(this.getX() < -r || this.getX() > GameManager.screen.getWidth() - TileMapRenderer.offsetX + r ||
                this.getY() < -r || this.getY() > GameManager.screen.getHeight() + r){
            return true;
        }
        
        
        return false;
        
    }
    
    public double getSpeed(){
        return this.speed;
    }
    public void setLive(boolean l){
        this.live = l;
    }
    
    public void draw(Graphics2D g){
        //g.setColor(color1);
        //g.fillOval((int)(x - r), (int)(y - r), 2 * r, 2 * r);
        g.drawImage(this.getImage(), Math.round(this.getX()), Math.round(this.getY()), null);
    }
    public Object clone() {
        // use reflection to create the correct subclass
        Constructor constructor = getClass().getConstructors()[0];
        try {
            return constructor.newInstance(
                new Object[] {(Animation)anim.clone()});
        }
        catch (Exception ex) {
            // should never happen
            ex.printStackTrace();
            return null;
        }
    }


}
