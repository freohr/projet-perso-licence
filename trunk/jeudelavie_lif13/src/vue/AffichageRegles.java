/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vue;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

/**
 *
 * @author leo
 */
public class AffichageRegles extends JFrame implements ItemListener, MouseListener{
    Regle regles[];
    
    public AffichageRegles()
    {
        super();
        buildRegles();
        buildWindow();
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent arg0) {
                boolean close = true;
                // si jamais on met une interruption
                if (close)
                {
                    dispose();
                }
            }
        });
    }
    
    private void buildRegles()
    {
        this.regles = new Regle[2];
        this.regles[0] = new Regle("Super cellules", false, "Super cellule vas sauver la terre !");
        this.regles[1] = new Regle("Cellules positives/negatives", false, "Posipi et Negapi sont sur un bateau, Posipi tombe à l'eau...");
    }
    
    private void buildWindow()
    {
        
        setTitle("Le jeu de la vie - Règles");
        //a modifier
        setSize(250, 300);
        setResizable(false);
        //buildMenu();
        JPanel panel = new JPanel(new BorderLayout());

        // Interface
        panel.add(buildInterface(), BorderLayout.CENTER);

        // boutons
        panel.add(buildButtons(), BorderLayout.PAGE_END);
        
       // panelPrincipal = panel;
        add(panel);
    }
    
    private JPanel buildInterface()
    {      
        JPanel panel = new JPanel(new GridLayout(0,1));
        Border blackline = BorderFactory.createLineBorder(Color.black, 1);
        
        for (Regle regle : this.regles) {
            panel.add(BuildExplicationItem(regle));
        }
        //creation de l'asecenseur
        panel.setBorder(blackline);
        JScrollPane ascenseur = new JScrollPane(panel);
        
        return panel;
    }
    
    private JPanel BuildExplicationItem (Regle regle)
    {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel subPanel = new JPanel (new BorderLayout());
        Border blackline = BorderFactory.createLineBorder(Color.black, 1);
        
        regle.checkBox.addItemListener(this);
        
        subPanel.setName("sub"+regle.checkBox.getText());
        subPanel.add(regle.checkBox, BorderLayout.CENTER);
        subPanel.setBorder(blackline);
        
        panel.setName("panel"+regle.checkBox.getText());
        panel.add(subPanel, BorderLayout.PAGE_START);
        panel.add(regle.explication, BorderLayout.CENTER);
        panel.setBorder(blackline);
        
        return panel;
    }

    private JPanel buildButtons()
    {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 10, 5, 10);
        
        JButton annuler = new JButton("Annuler");
        annuler.addItemListener(this);
        annuler.setName("BoutonAnnuler");
        c.anchor = GridBagConstraints.LINE_START;
        panel.add(annuler, c);
        
        JButton enregistrer = new JButton("Enregistrer");
        enregistrer.addItemListener(this);
        enregistrer.setName("BoutonEnregistrer");
        c.anchor = GridBagConstraints.LINE_END;
        panel.add(enregistrer, c);
        
        return panel;
    }
    
    @Override
    public void itemStateChanged(ItemEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mousePressed(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
