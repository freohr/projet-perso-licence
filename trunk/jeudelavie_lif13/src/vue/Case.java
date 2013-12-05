/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vue;

import Controleur.Controle;
import Utils.CellStates;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

/**
 *
 * @author p1006149
 */
public class Case extends JPanel {

    protected int x;
    protected int y;
    protected Controle controle;

    public Case(int x, int y, Controle controle) {
        super();
        this.x = x;
        this.y = y;
        this.controle = controle;
        setBackground(Color.white);
        addMouseListener(new ClickListener());
    }

    public Case(int x, int y) {
        super();
        this.x = x;
        this.y = y;
        setBackground(Color.white);
    }

    public void setCaseColor(int color) {
        switch (color) {
            case CellStates.ALIVE:
                setBackground(new Color(51,153,51));
                break;

            case CellStates.DEAD:
                setBackground(new Color(245,245,245));
                break;

            default:
                break;
        }
    }

    public Color getCaseColor() {
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
