/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author p1006099
 */
public class CellStates {

    public final static int DEAD = 0;
    public final static int ALIVE = 1;
    public final static int IMMORTAL = 2;
    public final static int POSITIVE = 3;
    public final static int NEGATIVE = 4;
    public final static int IMMORTAL_POSITIVE = 5;
    public final static int IMMORTAL_NEGATIVE = 6;
    public final static int UNDER_MOTIF = 7;
    
    public final static Map<Integer, Color> colors;

    static {
        colors = new HashMap<>();
        colors.put(DEAD, new Color(245, 245, 245));
        colors.put(ALIVE, new Color(50, 205, 50));
        colors.put(IMMORTAL, new Color(0, 100, 0));
        /* passer les couleurs en rgb */
        colors.put(POSITIVE, new Color(0,191,255)); // cyan
        colors.put(NEGATIVE, new Color(255, 0, 255)); // magenta
        colors.put(IMMORTAL_POSITIVE, new Color(0, 0, 255)); //bleu
        colors.put(IMMORTAL_NEGATIVE, new Color(255, 0, 0)); //rouge
        colors.put(UNDER_MOTIF, new Color(230, 230, 10));
    }
}
