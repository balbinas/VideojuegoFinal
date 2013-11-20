/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.triton.tilegame.sprites;

import com.triton.Graphics.Animation;
import java.awt.Graphics; 
import java.awt.Color; 
import com.triton.Graphics.*;
import com.triton.tilegame.TileMap;

/**
 *
 * @author Balbina
 */
public class Shot extends Creature{
    // variables 
    private int x_pos; 
    private int y_pos; 
    

    // size of the shot 
    private final int radius = 3; 

    // constructor 
    public Shot(Animation left, Animation right, Animation deadLeft, Animation deadRight) 
    { 
        super(left,right,deadLeft,deadRight);
    } 

    
    public float getMaxSpeed() {
        return 0.2f;
    }


    public boolean isFlying() {
        return isAlive();
    }
    
    }
