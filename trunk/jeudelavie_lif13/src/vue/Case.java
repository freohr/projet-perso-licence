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
    public static boolean mouseDown = false;
    public static int mouseButton;

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
                setBackground(new Color(51, 153, 51));
                break;

            case CellStates.DEAD:
                setBackground(new Color(245, 245, 245));
                break;
                
            case CellStates.UNDER_MOTIF:
                setBackground(Color.red);

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
            if (controle.hasMotif()) {
                controle.applyMotif();
            } else {
                controle.changeCell(c.x, c.y);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            Case.mouseDown = true;
            Case.mouseButton = e.getButton();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            Case.mouseDown = false;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            Case c = (Case) e.getSource();
            if (Case.mouseDown && Case.mouseButton == MouseEvent.BUTTON1) {
                controle.setCellAlive(c.x, c.y);
            }

            if (Case.mouseDown && Case.mouseButton == MouseEvent.BUTTON3) {
                controle.setCellDead(c.x, c.y);
            }

            if (controle.hasMotif()) {
                controle.setOffset(c.x, c.y);
                controle.showMotif();
            }
        }
    }
}
