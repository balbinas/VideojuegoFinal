package com.triton.tilegame.sprites;

import com.triton.tilegame.TileMap;
import com.triton.Graphics.Animation;

/**
    The Player.
*/
public class Player extends Creature {

    private static final float JUMP_SPEED = -.95f;

    private boolean onGround;
    private boolean firing;
    private long bulletTimer;
    private long bulletDelay;

    public Player(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight);
        firing = false;
        bulletTimer = System.nanoTime();
        bulletDelay = 500;
    }


    public void collideHorizontal() {
        setVelocityX(0);
    }


    public void collideVertical() {
        // check if collided with ground
        if (getVelocityY() > 0) {
            onGround = true;
        }
        setVelocityY(0);
    }


    public void setY(float y) {
        // check if falling
        if (Math.round(y) > Math.round(getY())) {
            onGround = false;
        }
        super.setY(y);
    }


    public void wakeUp() {
        // do nothing
    }


    /**
        Makes the player jump if the player is on the ground or
        if forceJump is true.
    */
    public void jump(boolean forceJump) {
        if (onGround || forceJump) {
            onGround = false;
            setVelocityY(JUMP_SPEED);
        }
    }
    
    public float getMaxSpeed() {
        return 0.5f;
    }
    
     public void setBulletTimer(long timer){
        this.bulletTimer = timer;
    }
    /**
     * Get bullet Timer
     * @return bullet timer
     */
    public long getBulletTimer(){
        return this.bulletTimer;
    }
    
    /**
     * Get bullet Delay
     * @return bullet timer
     */
    public long getBulletDelay(){
        return this.bulletDelay;
    }
    
    /**
     * Is player firing?
     * @return firing
     */
    public boolean isFiring(){
        return this.firing;
    }
    
    /**
     * Makes the player fire 
     * @param fire 
     */
    public void fire(boolean fire) {
        this.firing = fire;
    }

}
