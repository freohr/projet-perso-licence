/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vue;

import Controleur.Controle;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
public class AffichageRegles extends JFrame implements ItemListener, ActionListener{
    Regle regles[];
    JButton annuler;
    JButton enregistrer;
    Controle controle;
    
    public AffichageRegles(Controle controle)
    {
        super();
        buildRegles();
        buildWindow();
        setLocationByPlatform(true);
        this.controle = controle;
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent arg0) {
                annuler();
            }
        });
        
        addWindowFocusListener(new WindowAdapter () {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                synchronizeCheckbox();
            }
        
        });
    }
    
    private void buildRegles()
    {
        this.regles = new Regle[2];
        this.regles[0] = new Regle("Super cellules", false, "Quand une cellule survit plus de cinq générations, elle devient immortelle");
        this.regles[1] = new Regle("Cellules positives/negatives", false, "Les cellules ont une charge positive ou negative, si une cellule "
                + "                                                    a plus de trois voisines de la même charge, elle passe à cette charge");
    }
    
    private void buildWindow()
    {
        
        setTitle("Règles");
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
        
        JButton ann = new JButton("Annuler");
        ann.addActionListener(this);
        ann.setName("BoutonAnnuler");
        c.anchor = GridBagConstraints.LINE_START;
        panel.add(ann, c);
        
        JButton enr = new JButton("Enregistrer");
        enr.addActionListener(this);
        enr.setName("BoutonEnregistrer");
        c.anchor = GridBagConstraints.LINE_END;
        panel.add(enr, c);
        
        this.annuler = ann;
        this.enregistrer = enr;
        
        return panel;
    }
    
    // listener des checkboxes
    @Override
    public void itemStateChanged(ItemEvent e) {
        Object source = e.getItemSelectable();

        for (Regle regle : this.regles)
        {
            if (source == regle.checkBox)
            {
                regle.active = (e.getStateChange() == ItemEvent.SELECTED);
                //System.out.println("la regle "+ regle.checkBox.getText() +" est passé à "+ regle.active);
            }
        }
    }
    
    // listener des boutons
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if(source == this.annuler)
            annuler();
        
        if(source == this.enregistrer)
            enregistrer();
    }
    
    private void synchronizeCheckbox()
    {
        for (Regle regle : this.regles)
            regle.checkBox.setSelected(regle.active);
    }
    
    private void annuler()
    {
        for (Regle regle : this.regles)
            regle.active = regle.wasActive;
        close();
    }
    
    private void enregistrer()
    {
        for (Regle regle : this.regles)
            regle.wasActive = regle.active;
        
        controle.modifRegles(regles[0].active, regles[1].active);
        close();
    }
    
    private void close ()
    {
        dispose();
    }
}
