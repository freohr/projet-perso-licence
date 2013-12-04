/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vue;

import java.awt.BorderLayout;
import Controleur.Controle;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JMenu;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import javax.swing.border.Border;
import modele.Monde;

/**
 *
 * @author p1006149
 */
public class Vue extends JFrame implements Observer {

    protected Grille g;
    protected JPanel panelPrincipal;
    protected JPanel panelGrid;
    protected Controle controle;
    protected final Monde monde;

    public Vue(int size) {
        super();

        g = new Grille(size, size);
        buildWindow(size);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent arg0) {
                super.windowClosing(arg0);
                System.exit(0);
            }
        });

    }
    
    public Vue(int size, Controle controle1) {
        super();

        g = new Grille(size, size);
        buildInterface(size);
        
        controle = controle1;

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent arg0) {
                super.windowClosing(arg0);
                System.exit(0);
            }
        });

    }
    
    public void setControler(Controle controle1) {
        this.controle = controle1;
    }

    private void buildWindow(int size) {

        setTitle("Le jeu de la vie");
        setSize(800, 800);
        buildMenu();
        JPanel panel = new JPanel(new BorderLayout());
        
        // Interface
        panel.add(buildInterface(size), BorderLayout.PAGE_START);

        // Grille
        
        panel.add(buildPanelGrid(size), BorderLayout.CENTER);

        //panel.add(buildButtons());
        panelPrincipal = panel;
        add(panelPrincipal);
    }
    
    private JPanel buildInterface(int size)
    {
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        JPanel panel = new JPanel(layout);
        panel.setName("interface");
        System.out.println("panel_name : " + panel.getName());

        c.anchor = GridBagConstraints.CENTER;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0.3;
        c.weighty = 0.3;

        // Colonne 1
        JLabel labelSize = new JLabel("Taille de la grille");
        labelSize.setHorizontalAlignment(JLabel.CENTER);
        labelSize.setName("labelSize");
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        panel.add(labelSize, c);

        JPanel panelTaux = new JPanel(layout);
        panelTaux.setName("panelTaux");

        JLabel labelTaux = new JLabel("Taux d'initialisation");
        labelTaux.setName("labelTaux");
        labelTaux.setHorizontalAlignment(JLabel.CENTER);
        c.gridx = 0;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        panelTaux.add(labelTaux, c);

        JLabel labelPercent = new JLabel("(% de cellules vivantes)");
        labelPercent.setName("labelPercent");
        labelPercent.setHorizontalAlignment(JLabel.CENTER);
        c.gridx = 0;
        c.gridy = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        panelTaux.add(labelPercent, c);

        c.gridx = 0;
        c.gridy = 1;
        panel.add(panelTaux, c);

        JLabel labelForme = new JLabel("Forme des cellules");
        labelForme.setHorizontalAlignment(JLabel.CENTER);
        c.gridx = 0;
        c.gridy = 2;
        c.fill = GridBagConstraints.BOTH;
        panel.add(labelForme, c);

        //Colonne 2
        JTextField textFieldTaille = new JTextField(new String() + size);
        textFieldTaille.setName("textFieldTaille");
        c.gridx = 1;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(textFieldTaille, c);

        JSlider sliderTaux = new JSlider(1, 100, 50);
        sliderTaux.setName("sliderTaux");
        c.gridx = 1;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(sliderTaux, c);

        String[] cells = {"Carré", "Hexagone", "Triangle"};
        JComboBox<String> listCells = new JComboBox<>(cells);
        listCells.setName("listCells");
        c.gridx = 1;
        c.gridy = 2;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        panel.add(listCells, c);

        // Colonne 3
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;

        JButton boutonInit = new JButton("Initialiser");
        boutonInit.setName("boutonName");
        c.gridx = 2;
        c.gridy = 0;
        boutonInit.addActionListener(new InitListener());
        panel.add(boutonInit, c);

        JButton boutonPause = new JButton("Pause");
        boutonPause.setName("boutonPause");
        boutonPause.addActionListener(new PauseListener());
        c.gridx = 2;
        c.gridy = 1;
        panel.add(boutonPause, c);

        JButton boutonStop = new JButton("Stop");
        boutonStop.setName("boutonStop");
        //boutonStop.addActionListener(new StopListener());
        c.gridx = 2;
        c.gridy = 2;
        panel.add(boutonStop, c);

        // Slider de vitesse (ligne 4)
        JLabel labelVitesse = new JLabel("Vitesse de génération (rapide <-> lent)");
        labelVitesse.setName("labelVitesse");
        c.gridx = 0;
        c.gridy = 3;
        panel.add(labelVitesse, c);

        JSlider sliderVitesse = new JSlider(10, 2000, 1005);
        sliderVitesse.setName("sliderVitesse");
        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        panel.add(sliderVitesse, c);
        
        return panel;
    }

    private void buildMenu() {
        JMenuBar jm = new JMenuBar();

        JMenu m = new JMenu("Jeu");

        JMenuItem mi = new JMenuItem("Partie");

        m.add(mi);

        jm.add(m);

        setJMenuBar(jm);
    }

    private JPanel buildPanelGrid(int size)
    {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setName("PanelGrid");
        GridBagConstraints c = new GridBagConstraints();
            
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        
        panel.add(buildGrid(size), c);
        return panel;
    }
    
    private JPanel buildGrid(int size) {
        JPanel panel = new JPanel(new GridLayout(size, size));
        Border blackline = BorderFactory.createLineBorder(Color.black, 1);
        panel.setName("grid");

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                panel.add(g.grille[i][j]);
                g.grille[i][j].setBorder(blackline);
            }
        }
        panel.setBorder(blackline);
        panelGrid = panel;
        return panel;

    }

    public void updateGrille(Monde world) {
        //System.out.println("Update grille");
        /*
         * System.out.println("monde.size " + world.getSize());
         * System.out.println("grille.size_x " + g.getSizeX());
         */

        // Si la grille du monde à afficher est différente de la grille d'affichage de la vue (en cas de nouveau monde par ex.)

        if (world.getSize() != g.getSizeX() || world.getSize() != g.getSizeY()) {

            System.out.println("Taille différente");

            Grille tmp = new Grille(world.getSize(), world.getSize());
            this.g = tmp;

            JComponent tmpGrid = buildGrid(world.getSize());
            tmpGrid.setName("grid");
            
            System.out.println("début de modification");
            GridBagConstraints c = new GridBagConstraints();
            
            c.anchor = GridBagConstraints.CENTER;
            c.fill = GridBagConstraints.NONE;
            
            Container ref = panelGrid.getParent();
            ref.remove(panelGrid);
            panelGrid = (JPanel) tmpGrid;
            ref.add(panelGrid, c);

            
                    SwingUtilities.updateComponentTreeUI(panelPrincipal);
        }

        //System.out.println("modif état");
        for (int i = 0; i < world.getSize(); i++) {
            for (int j = 0; j < world.getSize(); j++) {
                
                if (world.getCellule(i, j).isAlive()) {
                    g.grille[i][j].setCaseColor(0);
                } else {
                    g.grille[i][j].setCaseColor(1);
                }
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        //System.out.println("update vue");
        updateGrille((Monde) o);


    }
    
    

    // Les event Listeners
    private class InitListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            //System.out.println("appui bouton");
            JComponent c = (JComponent) e.getSource();

            for (Component c2 : c.getParent().getComponents()) {
                if (c2 instanceof JTextField) {
                    JTextField jTextField = (JTextField) c2;

                    //System.out.println("textfield");
                    //System.out.println(jTextField.getText());

                    controle.initMonde(new Integer(jTextField.getText()));
                }
            }
        }
    }
    
    private class PauseListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            controle.pause();
        }
        
    }
    
    private class StopListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            
            controle.stop();
        }
        
    }
}
