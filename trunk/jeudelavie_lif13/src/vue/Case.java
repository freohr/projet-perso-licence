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
    
    public Case ()
    {
        super();
        
        setBackground(Color.white);
    }
    
    public void setCaseColor (int color)
    {
        switch (color)
        {
            case 0:
                setBackground(Color.green);
                break;
            
            case 1:
                setBackground(Color.white);
                break;
                
            default :
                break;
        }
    }
}
