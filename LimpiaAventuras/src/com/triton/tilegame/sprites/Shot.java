package com.triton.tilegame.sprites;

import com.triton.Graphics.Sprite;
import com.triton.Graphics.Animation;
import java.lang.reflect.Constructor;
import com.triton.Graphics.*;

/**
    A PowerUp class is a Sprite that the player can pick up.
*/
public abstract class Shot extends Sprite {

    public Shot(Animation anim) {
        super(anim);
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


    /**
        A Star PowerUp. Gives the player points.
    */
    public static class Bala extends Shot {
        public Bala(Animation anim) {
            super(anim);
        }
    }
    
}


