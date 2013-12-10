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

    public final static int ALIVE = 1;
    public final static int DEAD = 0;
    public final static int UNDER_MOTIF = 10;
    
    public final static int TEAM_1 = 2;
    public final static int TEAM_2 = 3;
    public final static int TEAM_3 = 4;
    public final static int TEAM_4 = 5;
    public final static Map<Integer, Color> teams;

    static {
        teams = new HashMap<>();
        teams.put(TEAM_1, Color.BLUE);
        teams.put(TEAM_2, Color.RED);
        teams.put(TEAM_3, Color.YELLOW);
        teams.put(TEAM_4, Color.CYAN);
    }
}
