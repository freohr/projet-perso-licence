/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vue;

import java.awt.Color;
import javax.swing.JPanel;

/**
 *
 * @author p1006149
 */
public class Case extends JPanel{
    public static final int ALIVE = 0;
    public static final int DEAD = 1;
    
    /** TODO
     * définir les couleurs des équipes
     * rajouter les numéros des équipes en constante statique
     */
    
    
    public Case ()
    {
        super();
        
        setBackground(Color.white);
    }
    
    public void setCaseColor (int color)
    {
        switch (color)
        {
            case Case.ALIVE:
                setBackground(Color.green);
                break;
            
            case Case.DEAD:
                setBackground(Color.white);
                break;
                
            default :
                break;
        }
    }
}
