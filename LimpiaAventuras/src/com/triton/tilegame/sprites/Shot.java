/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.triton.tilegame.sprites;

import com.triton.Graphics.Animation;
import java.awt.Graphics; 
import java.awt.Color; 
import com.triton.Graphics.*;

/**
 *
 * @author Balbina
 */
public class Shot{
    // variables 
    private int x_pos; 
    private int y_pos; 
    

    // size of the shot 
    private final int radius = 3; 

    // constructor 
    public Shot(float x, float y) 
    { 
    x_pos = (int)x; 
    y_pos = (int)y; 
    } 
    
    public Shot(int x, int y) 
    { 
    x_pos = x; 
    y_pos = y; 
    } 


    // returns y - position, needed for testing if shot has left the game area 
    public int getYPos() 
    { 
    return y_pos; 
    } 
    
    public int getXPos(){
        return x_pos;
    }

    // moving shot in y - direction 
    public void moveShot(int speed) 
    { 
    y_pos += speed; 
    } 

    // draw the shot to the screen 
    public void drawShot(Graphics g) 
    { 
    g.setColor(Color.yellow); 
    g.fillOval(x_pos, y_pos, radius, radius); 
    } 
    }
