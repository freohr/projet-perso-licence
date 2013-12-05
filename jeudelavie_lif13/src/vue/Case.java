/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vue;

import Controleur.Controle;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

/**
 *
 * @author p1006149
 */
public class Case extends JPanel{
    public static final int ALIVE = 0;
    public static final int DEAD = 1;
    
    protected int x;
    protected int y;
    protected Controle controle;
    
    /** TODO
     * définir les couleurs des équipes
     * rajouter les numéros des équipes en constante statique
     */
    
    
    public Case (int x, int y, Controle controle)
    {
        super();
        this.x = x;
        this.y = y;
        this.controle = controle;
        setBackground(Color.white);
        addMouseListener(new ClickListener()); 
    }
    
    public Case (int x, int y)
    {
        super();
        this.x = x;
        this.y = y;
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
    
    public Color getCaseColor()
    {
        return this.getBackground();
    }
    
    private class ClickListener extends MouseAdapter {
        @Override
            public void mouseClicked(MouseEvent e) {
                Case c = (Case) e.getSource();
                controle.changeCell(c.x, c.y);
            }
    }
}
