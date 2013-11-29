/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vue;

import java.awt.Color;
import java.awt.GridLayout;

import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JMenu;

import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicComboBoxUI.ItemHandler;
import modele.Monde;
/**
 *
 * @author p1006149
 */
public class Vue extends JFrame implements Observer{
    Grille g;
    JPanel panelPrincipal;
    
    public Vue(int size)
    {
        super();
        panelPrincipal = new JPanel();
        g = new Grille(size, size);
        buildInterface(size);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent arg0) {
                super.windowClosing(arg0);
                System.exit(0);
            }
        });
        
    }
    
    private void buildInterface(int size)
    {
        setTitle("Le jeu de la vie");
        setSize(600, 600);
        buildMenu();
        JComponent panel = new JPanel();
        panel.add(buildGrid(size));
        panel.add(buildButtons());
        add(panel);
    }
    
    private void buildMenu()
    {
        JMenuBar jm = new JMenuBar();
        
        JMenu m = new JMenu("Jeu");
        
        JMenuItem mi = new JMenuItem("Partie");
        
        m.add(mi);
        
        jm.add(m);
        
        setJMenuBar(jm);
    }
    
    private JComponent buildGrid(int size)
    {

        JComponent panel = new JPanel (new GridLayout(size, size));
        Border blackline = BorderFactory.createLineBorder(Color.black,1);
        
        for(int i = 0; i<size; i++)
        {
            for(int j = 0; j<size; j++)
            {
                panel.add(g.grille[i][j]);
                g.grille[i][j].setBorder(blackline);
            }
        }
        panel.setBorder(blackline);
        return panel;
        
    }
    
    private JComponent buildButtons()
    {
        JComponent panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        JButton butt = new JButton("recommencer");
        panel.add(butt);
        JButton butt2 = new JButton("pause");
        panel.add(butt2);
        JButton butt3 = new JButton("continuer");
        panel.add(butt3);
        return panel;
    }
    
    public void update(Monde world)
    {
        for (int i = 0; i<world.getSize(); i++)
        {
            for (int j=0; j<world.getSize(); j++)
            {
                if(world.getCellule(i, j).isAlive())
                    g.grille[i][j].setCaseColor(0);
                else
                    g.grille[i][j].setCaseColor(1);
            }
        }
        
        
    }

    @Override
    public void update(Observable o, Object arg) {
        update((Monde) o);
    }
}
